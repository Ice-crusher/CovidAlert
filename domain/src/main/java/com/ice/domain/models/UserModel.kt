package com.ice.domain.models

data class UserModel(
    val email: String,
    val userId: String,
    val fcmToken: String,
    val mainWebSiteUrl: String
)