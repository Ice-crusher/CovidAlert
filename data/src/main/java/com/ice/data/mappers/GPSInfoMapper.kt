package com.ice.data.mappers

import com.google.gson.Gson
import com.ice.domain.models.GPSInfoModel
import javax.inject.Inject

class GPSInfoMapper @Inject constructor() {

    fun toGPSInfo(gpsInfoStringJson: String): GPSInfoModel? {
        if (gpsInfoStringJson.isEmpty()) return null
        try {
            val gpsInfoJson = Gson().fromJson(gpsInfoStringJson, GPSInfoModel::class.java)
            return GPSInfoModel(
                geographicCoordinateX = gpsInfoJson.geographicCoordinateX,
                geographicCoordinateY = gpsInfoJson.geographicCoordinateY,
                time = gpsInfoJson.time
            )
        } catch (e: Exception) {
            return null
        }
    }

    fun toStringJson(gpsInfoModel: GPSInfoModel?): String? {
        try {
            return Gson().toJson(gpsInfoModel)
        } catch (e: Exception) {
            return null
        }
    }
}