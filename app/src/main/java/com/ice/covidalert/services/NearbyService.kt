package com.ice.covidalert.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.gson.Gson
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.covidalert.viewmodel.MainViewModel
import com.ice.data.models.NearbyMessageJson
import com.ice.data.repo.CredentialsRepoImpl
import com.ice.domain.usecases.nearbyTouch.NearbyTouchCase
import com.ice.domain.usecases.sick.SickUseCase
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

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

        mMessageListener = object : MessageListener() {
            override fun onFound(message: Message) {
                Log.d(TAG, "Found message: ${String(message.content)}")
                val nearbyMessage =
                    Gson().fromJson(String(message.content), NearbyMessageJson::class.java)
                Log.d(TAG, "Found message: " + String(message.content))

                Toast.makeText(this@NearbyService, "Found user near!", Toast.LENGTH_SHORT).show()

                nearbyTouchCase.execute(NearbyTouchCase.Params(
                        credentialsRepoImpl.getUserId(),
                        nearbyMessage.userId
                    ))
                    .subscribeOn(schedulers.io())
                    .observeOn(schedulers.ui())
                    .subscribe({
                        it?.let {
                            Log.d(NearbyService::class.java.simpleName, "Success send nearbyTouch to the server!")
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
//        startPublishMessage(this, Message("Some string".toByteArray()))
//        subscribe(this)
        Log.d(TAG, "onCreate() Service")
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
//            .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
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

    fun stopPublishMessage(context: Context, message: Message) {
        Nearby.getMessagesClient(context).unpublish(message)
            .addOnSuccessListener {
//                Toast.makeText(BaseApp.instance, "Success unpublish message", Toast.LENGTH_SHORT).show()
//                preferenceHelper.removePublishMessage(message) // todo
                Log.d(TAG, "Success unpublish message")
            }
            .addOnCanceledListener {
//                Toast.makeText(BaseApp.instance, "Cancel unpublish message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Cancel unpublish message")
            }
            .addOnFailureListener {
//                Toast.makeText(BaseApp.instance, "Failure unpublish message", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Failure unpublish message")
            }
    }

//    fun stopAllPublishMessage(context: Context) {
//        val cachedMessages = ArrayList(preferenceHelper.getPublishMessages())
//        for (item in cachedMessages) {
//            Log.d(TAG, "Detected item: " + item.toString())
//            stopPublishMessage(context, Message(item.toByteArray()))
//        }
////        val cachedMessagesSet = HashSet(cachedMessages)
////        if (!cachedMessagesSet.contains(messageString)) {
////            cachedMessages.add(0, String(message.content))
////        }
//
//        // delete all from Preferences Published Messages
//    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy() Service")
        unubscribe(applicationContext)
//        stopPublishMessage()
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
    }

    override fun onBind(p0: Intent?): IBinder? {
        return mBinder
    }

//    private fun getPendingIntent(): PendingIntent {
//        return PendingIntent.getBroadcast(
//            this, 0, Intent(this, BeaconMessageReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//    }
}