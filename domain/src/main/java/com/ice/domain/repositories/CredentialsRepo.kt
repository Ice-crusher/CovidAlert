package com.ice.domain.repositories

import com.ice.domain.models.GPSInfoModel

interface CredentialsRepo {

    fun getFCMToken(): String
    fun getEmail(): String
    fun getUserId(): String
    fun getWebLink(): String
    fun getLastGPSInfo(): GPSInfoModel?
    fun getInstanceId(): String

    fun setFCMToken(fcmToken: String)
    fun setEmail(email: String)
    fun setUserId(userId: String)
    fun setWebLink(webLink: String)
    fun setLastGPSInfo(geographicCoordinateX: Float?, geographicCoordinateY: Float?, time: Long)
    fun setInstanceId(instanceId: String)
}