package com.ice.data.models

import com.google.gson.annotations.SerializedName

data class NearbyMessageJson (
    @SerializedName("userId")
    var userId: String
)