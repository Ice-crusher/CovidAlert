package com.ice.domain.repositories

interface CredentialsRepo {

    fun getFCMToken(): String
    fun getEmail(): String
    fun getUserId(): String
    fun getWebLink(): String

    fun setFCMToken(fcmToken: String)
    fun setEmail(email: String)
    fun setUserId(userId: String)
    fun setWebLink(webLink: String)
}