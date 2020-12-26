package com.ice.domain.usecases.nearbyTouch

import com.google.gson.JsonObject
import com.ice.domain.repositories.RemoteRepo
import com.ice.domain.usecases.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class NearbyTouchCase @Inject constructor(
    private val apiRepo: RemoteRepo
): SingleUseCase<NearbyTouchCase.Params, JsonObject> {
    override fun execute(params: Params): Single<JsonObject> {
        return apiRepo.nearbyTouch(
            myUserId = params.myUserId,
            opponentId = params.opponentId
        )
    }

    data class Params(
        val myUserId: String,
        val opponentId: String
    )
}
