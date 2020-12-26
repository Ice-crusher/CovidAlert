package com.ice.domain.usecases.login

import com.ice.domain.models.UserModel
import com.ice.domain.repositories.RemoteRepo
import com.ice.domain.usecases.SingleUseCase
import io.reactivex.Single
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val apiRepo: RemoteRepo
): SingleUseCase<LoginUseCase.Params, UserModel> {
    override fun execute(params: Params): Single<UserModel> {
        return apiRepo.login(
            email = params.email,
            fcmToken = params.fcmToken
        )
    }

    data class Params(
        val email: String,
        val fcmToken: String
    )
}

