package com.ice.data.models

import com.google.gson.annotations.SerializedName

data class UserJson(
    @SerializedName("secretValue")
    val secretValue: String,
    @SerializedName("userId")
    val userId: String,
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("mainWebSiteUrl")
    val mainWebSiteUrl: String?
)