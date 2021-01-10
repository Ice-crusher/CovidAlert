package com.ice.covidalert.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.material.snackbar.Snackbar
import com.ice.covidalert.BuildConfig
import com.ice.covidalert.R
import com.ice.covidalert.databinding.ActivityMainBinding
import com.ice.covidalert.di.obtainViewModel
import com.ice.covidalert.services.NearbyService
import com.ice.covidalert.ui.common.BaseActivity
import com.ice.covidalert.ui.dialogs.SickDialog
import com.ice.covidalert.viewmodel.MainViewModel
import com.ice.covidalert.web.CustomWebViewClient
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject


class MainActivity: BaseActivity(), NearbyService.NearbyServiceCallback {

    var TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    protected lateinit var mService: NearbyService
    private var mBound: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel : MainViewModel

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as NearbyService.LocalBinder
            mService = binder.getService()
            mService.startPublish(this@MainActivity)
            mService.subscribe(this@MainActivity)
            mService.listener = this@MainActivity
            mBound = true
//            onNearbyConnect()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
//            onNearbyDisconnect()
        }
    }

    override fun onStart() {
        super.onStart()

        if (mBound) {
            mService.startPublish(this@MainActivity)
            mService.subscribe(this@MainActivity)
        }
        // Bind to LocalService
        Intent(this, NearbyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
//        unbindService(connection)
//        mBound = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = viewModelFactory.obtainViewModel(this)

        viewModel.isLoading.observe(this,
            Observer {
                if (it.peekContent()) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        )
        viewModel.toastMessage.observe(this,
            Observer {
                makeToast(it.peekContent())
            }
        )

        viewModel.webViewLink.observe(this,
            Observer { link ->
                link.getContentIfNotHandled()?.let {
                    // Set web view
                    mainContentWebView.settings.javaScriptEnabled = true
                    mainContentWebView.webViewClient = CustomWebViewClient(it)
                    mainContentWebView.loadUrl(it)
                }
            })

        view.button.setOnClickListener {
            SickDialog.newInstance(this@MainActivity, View.OnClickListener {
                    viewModel.onClickSick()
                })?.show()
        }

        requestPermissionsIfNeeded()
        viewModel.onCreate()
    }

    private fun requestPermissionsIfNeeded() {
        if (!permissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )

            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun permissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.startLocationUpdates()
            } else {
                // Permission denied.
                Snackbar.make(
                    binding.root,
                    R.string.permission_denied_explanation,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setTextColor(Color.WHITE)
                    .setAction(
                        R.string.settings,
                        View.OnClickListener { // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        })
                    .show()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && mainContentWebView.canGoBack()) {
            mainContentWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    override fun showTurnOnGPSDialog(exception: ResolvableApiException) {
        // Show the dialog by calling startResolutionForResult(),
        // and check the result in onActivityResult().
        exception.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 111
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 112
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}