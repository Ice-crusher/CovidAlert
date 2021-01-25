package com.ice.data.apiservice

import com.google.gson.JsonObject
import com.ice.data.models.HistoryJson
import com.ice.data.models.UserJson
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/sick")
    fun sick(@Body params: HashMap<String, String>): Single<JsonObject>

    @POST("/nearbyTouch")
    fun nearbyTouch(@Body params: HashMap<String, String>): Single<JsonObject>

    @POST("/login")
    fun login(@Body params: HashMap<String, String>): Single<UserJson>

    @GET("/userTouchHistory")
    fun history(@Query("userId") userId: String): Single<HistoryJson>
}