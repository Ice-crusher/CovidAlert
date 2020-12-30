package com.ice.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

class PreferenceImpl @Inject constructor(
    var preferences: SharedPreferences
) : PreferenceHelper {

    companion object {
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_USER_EMAIL = "PREF_USER_EMAIL"
        const val PREF_USER_ID = "PREF_USER_ID"
        const val PREF_WEB_CONTENT_LINK = "PREF_WEB_CONTENT_LINK"
    }

    override fun getFCMToken(): String {
        return getEmptyStringOrValue(PREF_FCM_TOKEN)
    }

    override fun setFCMToken(fcmToken: String) {
        preferences.edit()
            .putString(PREF_FCM_TOKEN, fcmToken)
            .apply()
    }

    override fun getEmail(): String {
        return getEmptyStringOrValue(PREF_USER_EMAIL)
    }

    override fun setEmail(email: String) {
        preferences.edit()
            .putString(PREF_USER_EMAIL, email)
            .apply()
    }

    override fun getUserId(): String {
        return getEmptyStringOrValue(PREF_USER_ID)
    }

    override fun setUserId(userId: String) {
        preferences.edit()
            .putString(PREF_USER_ID, userId)
            .apply()
    }

    override fun getWebContentLink(): String {
        return getEmptyStringOrValue(PREF_WEB_CONTENT_LINK)
    }

    override fun setWebContentLink(webLink: String) {
        preferences.edit()
            .putString(PREF_WEB_CONTENT_LINK, webLink)
            .apply()
    }

    private fun getEmptyStringOrValue(key: String): String {
        return preferences.getString(key, "") ?: ""
    }
}