package com.ice.covidalert.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
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

    override fun onNewToken(token: String) {
        Log.d(TAG, "Token is: $token")
        credentialsRepo.setFCMToken(token)
    }
}