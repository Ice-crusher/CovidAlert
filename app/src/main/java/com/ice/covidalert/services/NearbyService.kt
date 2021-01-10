package com.ice.covidalert.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.data.models.NearbyMessageJson
import com.ice.data.repo.CredentialsRepoImpl
import com.ice.domain.usecases.nearbyTouch.NearbyTouchCase
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class NearbyService : Service() {

    var TAG = "NearbyService"

//    @Inject
//    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var schedulers: SchedulersProvider

    @Inject
    lateinit var nearbyTouchCase: NearbyTouchCase

    @Inject
    lateinit var credentialsRepoImpl: CredentialsRepoImpl

    var listener: NearbyServiceCallback? = null
    private lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationRequest: LocationRequest? = null

    private val compositeDisposable = CompositeDisposable()
    // This is the object that receives interactions from clients.
    val mBinder = LocalBinder()

    lateinit var mMessageListener: MessageListener

//    private val TTL_IN_SECONDS = 3 * 60

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): NearbyService = this@NearbyService
    }


    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)

        setGPS()

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                Log.d(TAG, "Found message: ${String(message.content)}")
                val nearbyMessage =
                    Gson().fromJson(String(message.content), NearbyMessageJson::class.java)
                Log.d(TAG, "Found message: " + String(message.content))

                Toast.makeText(this@NearbyService, "Found user near!", Toast.LENGTH_SHORT).show()
                val lastGPSInfo = credentialsRepoImpl.getLastGPSInfo()

                nearbyTouchCase.execute(
                    NearbyTouchCase.Params(
                        myUserId = credentialsRepoImpl.getUserId(),
                        geographicCoordinateX = lastGPSInfo?.geographicCoordinateX,
                        geographicCoordinateY = lastGPSInfo?.geographicCoordinateY,
                        opponentId = nearbyMessage.userId
                    )
                )
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .subscribe({
                        it?.let {
                            Log.d(
                                NearbyService::class.java.simpleName,
                                "Success send nearbyTouch to the server!"
                            )
                        }
                    }, {
                        it.printStackTrace()
                        Log.e(NearbyService::class.java.simpleName, it.stackTrace.toString())
                        // error
                    }).let {
                        compositeDisposable.add(it)
                    }
            }

            override fun onLost(message: Message) {
                Log.d(TAG, "Lost sight of message: " + String(message.content))
                val nearbyMessage =
                    Gson().fromJson(String(message.content), NearbyMessageJson::class.java)
            }
        }
        Log.d(TAG, "onCreate() Service")
    }

    private fun setGPS() {
        // clear GPS values from preferences
        credentialsRepoImpl.setLastGPSInfo(null, null, System.currentTimeMillis())
        // set gps requests
        // set location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    //  save to preferences
                    credentialsRepoImpl.setLastGPSInfo(
                        geographicCoordinateX = location.latitude.toFloat(),
                        geographicCoordinateY = location.longitude.toFloat(),
                        time = System.currentTimeMillis())
//                    makeToast("accuracy: ${location.accuracy} - ${location.latitude}, ${location.longitude}")
                    Log.d(TAG, "accuracy: ${location.accuracy} - ${location.latitude}, ${location.longitude}")
                }
            }
        }
        createLocationRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChanel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "com.ice.covidalert"
        val channelName = "Location"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Content title")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }

    fun subscribe(context: Context) {
        Log.d(TAG, "Subscribing...")
//        val options = SubscribeOptions.Builder() // todo
//            .setStrategy(Strategy.DEFAULT)
//            .build()
        val options = SubscribeOptions.Builder()
            .setStrategy(Strategy.BLE_ONLY)
            .setCallback(object : SubscribeCallback() {
                override fun onExpired() {
                    super.onExpired()
                    Log.d(TAG, "No longer subscribing")
                    subscribe(context)
                }
            }).build()

        Nearby.getMessagesClient(context)
            .subscribe(mMessageListener, options)
            .addOnSuccessListener {
//                Toast.makeText(BaseApp.instance, "Success subscribe message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Success subscribe message")
            }
            .addOnCanceledListener {
//                Toast.makeText(BaseApp.instance, "Cancel subscribe message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Cancel subscribe message")
            }
            .addOnFailureListener {
//                Toast.makeText(BaseApp.instance, "Failure subscribe message", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failure subscribe message")
                Log.e(TAG, it.toString())
            }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationRequest?.let {
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(it)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException){
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        listener?.showTurnOnGPSDialog(exception)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    fun unubscribe(context: Context) {
        Log.d(TAG, "Unubscribing...")
        Nearby.getMessagesClient(context).unsubscribe(mMessageListener)
            .addOnSuccessListener {
//                Toast.makeText(BaseApp.instance, "Success unsubscribe message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Success unsubscribe message")
            }
            .addOnCanceledListener {
//                Toast.makeText(BaseApp.instance, "Cancel unsubscribe message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Cancel unsubscribe message")
            }
            .addOnFailureListener {
//                Toast.makeText(BaseApp.instance, "Failure unsubscribe message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failure unsubscribe message")
            }
    }

    fun startPublish(context: Context) {
        val s = Strategy.Builder()
            .build()
        val options = PublishOptions.Builder()
            .setStrategy(Strategy.BLE_ONLY)
            .setStrategy(s)
            .build()

        val userPublishMessage = NearbyMessageJson(credentialsRepoImpl.getUserId())
        val publishMessage = Message(Gson().toJson(userPublishMessage).toByteArray())

        Nearby.getMessagesClient(context).publish(publishMessage, options)
            .addOnSuccessListener {
//                Toast.makeText(BaseApp.instance, "Success publish: " + message.content.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Success publish: " + publishMessage.content.toString())
//                preferenceHelper.savePublishMessage(message) // todo
            }
            .addOnCanceledListener {
//                Toast.makeText(BaseApp.instance, "Cancel publish: " + message.content.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Cancel publish: " + publishMessage.content.toString())
            }
            .addOnFailureListener {
//                Toast.makeText(BaseApp.instance, "Failure publish: " + message.content.toString(), Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failure publish: " + publishMessage.content.toString())
                Log.e(TAG, it.toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy() Service")
        unubscribe(applicationContext)
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

    interface NearbyServiceCallback {
        fun showTurnOnGPSDialog(exception: ResolvableApiException)
    }
}