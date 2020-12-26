package com.ice.data.di

import com.ice.data.repo.RemoteRepoImpl
import com.ice.domain.repositories.RemoteRepo
import dagger.Module
import dagger.Provides

@Module
class RemoteModule {

    @Provides
    fun remoteRepoImp(remoteRepoImpl: RemoteRepoImpl): RemoteRepo {
        return remoteRepoImpl
    }
}