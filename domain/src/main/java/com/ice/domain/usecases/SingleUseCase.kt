package com.ice.domain.usecases

import io.reactivex.Single

interface SingleUseCase<in Params, Type> where Type : Any {
    fun execute(params: Params): Single<Type>
}