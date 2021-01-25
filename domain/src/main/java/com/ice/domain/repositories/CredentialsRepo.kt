package com.ice.domain.repositories

import com.ice.domain.models.GPSInfoModel

interface CredentialsRepo {

    fun getFCMToken(): String
    fun getSecretValue(): String
    fun getUserId(): String
    fun getWebLink(): String
    fun getLastGPSInfo(): GPSInfoModel?

    fun setFCMToken(fcmToken: String)
    fun setSecretValue(secretValue: String)
    fun setUserId(userId: String)
    fun setWebLink(webLink: String)
    fun setLastGPSInfo(geographicCoordinateX: Float?, geographicCoordinateY: Float?, time: Long)
}