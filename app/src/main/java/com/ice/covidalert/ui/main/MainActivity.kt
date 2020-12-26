package com.ice.covidalert.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.gson.Gson
import com.ice.covidalert.R
import com.ice.covidalert.databinding.ActivityMainBinding
import com.ice.covidalert.di.obtainViewModel
import com.ice.covidalert.services.NearbyService
import com.ice.covidalert.ui.common.BaseActivity
import com.ice.covidalert.viewmodel.MainViewModel
import com.ice.data.models.NearbyMessageJson
import com.ice.domain.usecases.nearbyTouch.NearbyTouchCase
import dagger.android.AndroidInjection
import dagger.android.DaggerActivity
import kotlinx.android.synthetic.main.activity_main.view.*
import javax.inject.Inject

class MainActivity: BaseActivity() {

    var TAG = "NearbyService"

    private lateinit var binding: ActivityMainBinding

    protected lateinit var mService: NearbyService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as NearbyService.LocalBinder
            mService = binder.getService()
            mService.startPublishMessage(this@MainActivity, Message("Some string".toByteArray()))
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
            mService.startPublishMessage(this@MainActivity, Message("Some string".toByteArray()))
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
//    val viewModel: MainViewModel by lazy {
//        ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
//    }

    private lateinit var viewModel : MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = viewModelFactory.obtainViewModel(this)

        view.button.setOnClickListener {
            viewModel.sick()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}