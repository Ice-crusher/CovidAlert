package com.ice.domain.usecases

import com.ice.domain.models.HistoryModel
import com.ice.domain.repositories.RemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class HistoryUseCase @Inject constructor(
    private val apiRepo: RemoteRepo
): SingleUseCase<HistoryUseCase.Params, HistoryModel> {

    override fun execute(params: Params): Single<HistoryModel> {
        return apiRepo.history(
            userId = params.userId
        )
    }

    data class Params(
        val userId: String
    )
}