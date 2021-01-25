package com.ice.data.repo

import com.google.gson.JsonObject
import com.ice.data.apiservice.ApiService
import com.ice.data.mappers.HistoryMapper
import com.ice.data.mappers.UserMapper
import com.ice.domain.models.HistoryModel
import com.ice.domain.models.UserModel
import com.ice.domain.repositories.RemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class RemoteRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val userMapper: dagger.Lazy<UserMapper>,
    private val historyMapper: dagger.Lazy<HistoryMapper>
) : RemoteRepo {

    override fun sick(userId: String): Single<JsonObject> {
        val parameters = HashMap<String, String>()
        parameters["userId"] = userId
        return apiService.sick(parameters)
    }

    override fun nearbyTouch(
        myUserId: String,
        geographicCoordinateX: Float?,
        geographicCoordinateY: Float?,
        opponentId: String
    ): Single<JsonObject> {
        val parameters = HashMap<String, String>()
        parameters["userId"] = myUserId
        geographicCoordinateX?.let {
            parameters["geographicCoordinateX"] =  it.toString()
        }
        geographicCoordinateY?.let {
            parameters["geographicCoordinateY"] =  it.toString()
        }
        parameters["opponentId"] = opponentId
        return apiService.nearbyTouch(parameters)
    }

    override fun login(secretValue: String, fcmToken: String): Single<UserModel> {
        val parameters = HashMap<String, String>()
        parameters["secretValue"] = secretValue
        parameters["fcmToken"] = fcmToken
        return apiService.login(parameters)
            .map {
                userMapper.get().toUserModel(it)
            }
    }

    override fun history(userId: String): Single<HistoryModel> {
        return apiService.history(userId)
            .map {
                historyMapper.get().toHistoryModel(it)
            }
    }
}