package com.ice.data.models


import com.google.gson.annotations.SerializedName

data class HistoryJson(
    @SerializedName("history")
    var history: List<History>
) {
    data class History(
        @SerializedName("geographicCoordinateX")
        var geographicCoordinateX: Double?,
        @SerializedName("geographicCoordinateY")
        var geographicCoordinateY: Double?,
        @SerializedName("time")
        var time: Long
    )
}