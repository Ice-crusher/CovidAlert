package com.ice.domain.repositories

import com.ice.domain.models.SickModel
import io.reactivex.Single

interface RemoteRepo {
    fun sick(): Single<SickModel>
}