package com.ice.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceImpl @Inject constructor(
    var preferences: SharedPreferences
) : PreferenceHelper {

    companion object {
        const val PREF_FCM_TOKEN = "PREF_FCM_TOKEN"
        const val PREF_USER_SECRET_VALUE = "PREF_USER_SECRET_VALUE"
        const val PREF_USER_ID = "PREF_USER_ID"
        const val PREF_WEB_CONTENT_LINK = "PREF_WEB_CONTENT_LINK"
        const val PREF_GPS_INFO = "PREF_GPS_INFO"
    }

    override fun getFCMToken(): String {
        return getEmptyStringOrValue(PREF_FCM_TOKEN)
    }

    override fun setFCMToken(fcmToken: String) {
        preferences.edit()
            .putString(PREF_FCM_TOKEN, fcmToken)
            .apply()
    }

    override fun getSecretValue(): String {
        return getEmptyStringOrValue(PREF_USER_SECRET_VALUE)
    }

    override fun setSecretValue(secretValue: String) {
        preferences.edit()
            .putString(PREF_USER_SECRET_VALUE, secretValue)
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

    override fun getLastGPSInfoJson(): String {
        return getEmptyStringOrValue(PREF_GPS_INFO)
    }

    override fun setLastGPSInfoJson(gpsInfoJson: String) {
        preferences.edit()
            .putString(PREF_GPS_INFO, gpsInfoJson)
            .apply()
    }

    private fun getEmptyStringOrValue(key: String): String {
        return preferences.getString(key, "") ?: ""
    }
}