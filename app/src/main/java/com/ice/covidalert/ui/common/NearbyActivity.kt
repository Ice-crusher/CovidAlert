package com.ice.covidalert.ui.common

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.material.snackbar.Snackbar
import com.ice.covidalert.BuildConfig
import com.ice.covidalert.R
import com.ice.covidalert.services.NearbyService

open class NearbyActivity: BaseActivity(), NearbyService.NearbyServiceCallback {
    private var TAG = "NearbyActivity"

    protected lateinit var mService: NearbyService
    protected var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    protected val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as NearbyService.LocalBinder
            mService = binder.getService()
            mService.startPublish(this@NearbyActivity)
            mService.subscribe(this@NearbyActivity)
            mService.listener = this@NearbyActivity
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()

        if (mBound) {
            mService.startPublish(this@NearbyActivity)
            mService.subscribe(this@NearbyActivity)
        }
        // Bind to LocalService
        Intent(this, NearbyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun showTurnOnGPSDialog(exception: ResolvableApiException) {
        // Show the dialog by calling startResolutionForResult(),
        // and check the result in onActivityResult().
        exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
    }

    protected fun requestPermissionsIfNeeded() {
        if (!permissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    protected fun permissionGranted(context: Context, permission: String): Boolean {
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
                    window.decorView,
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

    companion object {
        const val REQUEST_CHECK_SETTINGS = 111
        const val REQUEST_PERMISSIONS_REQUEST_CODE = 112
    }
}