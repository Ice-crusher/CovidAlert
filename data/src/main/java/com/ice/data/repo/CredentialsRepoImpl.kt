package com.ice.data.repo

import android.content.Context
import com.ice.data.R
import com.ice.data.preferences.PreferenceHelper
import com.ice.domain.repositories.CredentialsRepo
import javax.inject.Inject

class CredentialsRepoImpl @Inject constructor(
    private val context: Context,
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

    override fun getWebLink(): String {
        return if (preferenceHelper.getWebContentLink().isEmpty())
            "https://www.gov.pl/web/koronawirus/wiadomosci"
        else {
            preferenceHelper.getWebContentLink()
        }
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

    override fun setWebLink(webLink: String) {
        preferenceHelper.setWebContentLink(webLink)
    }
}