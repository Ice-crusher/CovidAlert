package com.ice.data.repo

import com.ice.data.apiservice.ApiService
import com.ice.data.mappers.SickMapper
import com.ice.domain.models.SickModel
import com.ice.domain.repositories.RemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class RemoteRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val sickMapper: SickMapper
) : RemoteRepo {

    override fun sick(): Single<SickModel> {
        return apiService.sick()
            .map {
                sickMapper.toSickModel(it)
            }
    }

}