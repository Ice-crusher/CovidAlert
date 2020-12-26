package com.ice.domain.usecases.sick

import com.google.gson.JsonObject
import com.ice.domain.models.SickModel
import com.ice.domain.repositories.RemoteRepo
import com.ice.domain.usecases.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class SickUseCase @Inject constructor(
    private val apiRepo: RemoteRepo
): SingleUseCase<SickUseCase.Params, JsonObject> {

    override fun execute(params: Params): Single<JsonObject> {
        return apiRepo.sick(
            userId = params.userId
        )
    }

    data class Params(
        val userId: String
    )
}