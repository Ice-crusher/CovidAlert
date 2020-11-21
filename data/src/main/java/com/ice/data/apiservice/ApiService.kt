package com.ice.data.apiservice

import com.ice.data.models.SickJson
import io.reactivex.Single
import retrofit2.http.POST

interface ApiService {

    @POST("/sick")
    fun sick(): Single<SickJson>
}