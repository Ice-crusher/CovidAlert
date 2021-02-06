package com.ice.domain.usecases

import com.ice.domain.models.UserModel
import com.ice.domain.repositories.RemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val apiRepo: RemoteRepo
): SingleUseCase<LoginUseCase.Params, UserModel> {
    override fun execute(params: Params): Single<UserModel> {
        return apiRepo.login(
            email = params.email,
            instanceId = params.instanceId,
            fcmToken = params.fcmToken
        )
    }

    data class Params(
        val email: String,
        val instanceId: String,
        val fcmToken: String
    )
}

