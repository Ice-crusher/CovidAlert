package com.ice.domain.usecases

import com.ice.domain.models.SickModel
import com.ice.domain.repositories.RemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class SickUseCase @Inject constructor(val apiRepo: RemoteRepo):
        SingleUseCase<SickModel> {

    override fun execute(): Single<SickModel> {
        return apiRepo.sick()
    }
}