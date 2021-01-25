package com.ice.domain.models

data class HistoryModel(
    var history: List<HistoryItem>
) {
    data class HistoryItem(
        var geographicCoordinateX: Double?,
        var geographicCoordinateY: Double?,
        var time: Long
    )
}