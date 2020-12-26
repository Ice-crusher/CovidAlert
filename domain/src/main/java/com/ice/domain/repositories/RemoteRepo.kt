package com.ice.domain.repositories

import com.google.gson.JsonObject
import com.ice.domain.models.UserModel
import com.ice.domain.models.SickModel
import io.reactivex.Single

interface RemoteRepo {
    fun sick(userId: String): Single<JsonObject>

    fun nearbyTouch(myUserId: String, opponentId: String): Single<JsonObject>

    fun login(email: String, fcmToken: String): Single<UserModel>

}