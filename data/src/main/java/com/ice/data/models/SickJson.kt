package com.ice.data.models

import com.google.gson.annotations.SerializedName

data class SickJson(
    @SerializedName("userId")
    val userId: String
)