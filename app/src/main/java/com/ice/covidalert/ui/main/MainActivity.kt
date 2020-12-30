package com.ice.covidalert.ui.main

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.messages.Message
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

class MainActivity: BaseActivity() {

    var TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    protected lateinit var mService: NearbyService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as NearbyService.LocalBinder
            mService = binder.getService()
            mService.startPublish(this@MainActivity)
            mService.subscribe(this@MainActivity)
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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel : MainViewModel

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
            // todo show dialog
            SickDialog.newInstance(this@MainActivity, View.OnClickListener {
                    viewModel.onClickSick()
                })?.show()
//            viewModel.sick()
        }

        viewModel.onCreate()
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

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}