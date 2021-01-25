package com.ice.domain.models

data class UserModel(
    val secretValue: String,
    val userId: String,
    val fcmToken: String,
    val mainWebSiteUrl: String
)