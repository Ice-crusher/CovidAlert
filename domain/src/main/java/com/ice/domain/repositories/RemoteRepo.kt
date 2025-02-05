package com.ice.domain.repositories

import com.google.gson.JsonObject
import com.ice.domain.models.HistoryModel
import com.ice.domain.models.UserModel
import io.reactivex.Single

interface RemoteRepo {
    fun sick(userId: String): Single<JsonObject>

    fun nearbyTouch(
        myUserId: String,
        geographicCoordinateX: Float?,
        geographicCoordinateY: Float?,
        opponentId: String
    ): Single<JsonObject>

    fun login(email: String, instanceId: String, fcmToken: String): Single<UserModel>

    fun history(userId: String): Single<HistoryModel>

}