package com.ice.data.preferences

interface PreferenceHelper {

    fun getFCMToken(): String
    fun setFCMToken(fcmToken: String)

    fun getEmail(): String
    fun setEmail(email: String)

    fun getUserId(): String
    fun setUserId(userId: String)

    fun getWebContentLink(): String
    fun setWebContentLink(webLink: String)

    fun getLastGPSInfoJson(): String
    fun setLastGPSInfoJson(gpsInfoJson: String)

    fun getInstanceId(): String
    fun setInstanceId(instanceId: String)
}