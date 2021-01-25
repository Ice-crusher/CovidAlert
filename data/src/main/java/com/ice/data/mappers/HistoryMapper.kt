package com.ice.data.mappers

import com.ice.data.models.HistoryJson
import com.ice.domain.models.HistoryModel
import java.util.*
import javax.inject.Inject

class HistoryMapper @Inject constructor() {

    fun toHistoryModel(historyJson: HistoryJson): HistoryModel {
        var historyList: ArrayList<HistoryModel.HistoryItem> = ArrayList()
        for (item in historyJson.history) {
            historyList.add(
                HistoryModel.HistoryItem(
                    geographicCoordinateX = item.geographicCoordinateX,
                    geographicCoordinateY = item.geographicCoordinateY,
                    time = item.time
                )
            )
        }
        return HistoryModel(
            history = historyList
        )
    }
}