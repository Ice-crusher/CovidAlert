package com.ice.data.mappers

import com.ice.data.models.SickJson
import com.ice.domain.models.SickModel
import javax.inject.Inject

class SickMapper @Inject constructor() {

    fun toSickModel(sickJson: SickJson): SickModel {
        return SickModel(
            userId = sickJson.userId
        )
    }
}