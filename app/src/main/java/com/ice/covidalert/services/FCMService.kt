package com.ice.covidalert.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ice.covidalert.R
import com.ice.covidalert.ui.main.LoginActivity
import com.ice.domain.repositories.CredentialsRepo
import dagger.android.AndroidInjection
import javax.inject.Inject


class FCMService : FirebaseMessagingService() {

    companion object {
        const val TAG = "FCMService"
    }

    @Inject
    lateinit var credentialsRepo: CredentialsRepo

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        Log.d(TAG, "FCMService created")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData().get("message"))
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Default"
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.notification?.title ?: getString(R.string.fcm_notification_title))
            .setContentText(remoteMessage.notification?.body ?: getString(R.string.fcm_notification_body))
            .setContentIntent(pendingIntent)
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Covid channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        manager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Token is: $token")
        credentialsRepo.setFCMToken(token)
    }
}