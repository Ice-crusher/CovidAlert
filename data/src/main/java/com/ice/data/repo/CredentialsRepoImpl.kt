package com.ice.data.repo

import com.ice.data.preferences.PreferenceHelper
import com.ice.domain.repositories.CredentialsRepo
import javax.inject.Inject

class CredentialsRepoImpl @Inject constructor(
    private val preferenceHelper: PreferenceHelper
) : CredentialsRepo {

    override fun getFCMToken(): String {
        return preferenceHelper.getFCMToken()
    }

    override fun getEmail(): String {
        return preferenceHelper.getEmail()
    }

    override fun getUserId(): String {
        return preferenceHelper.getUserId()
    }

    override fun setFCMToken(fcmToken: String) {
        preferenceHelper.setFCMToken(fcmToken)
    }

    override fun setEmail(email: String) {
        preferenceHelper.setEmail(email)
    }

    override fun setUserId(userId: String) {
        preferenceHelper.setUserId(userId)
    }
}