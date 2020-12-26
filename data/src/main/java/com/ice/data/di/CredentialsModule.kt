package com.ice.data.di

import com.ice.data.models.NearbyMessageJson
import com.ice.data.preferences.PreferenceHelper
import com.ice.data.preferences.PreferenceImpl
import com.ice.data.repo.CredentialsRepoImpl
import com.ice.data.repo.RemoteRepoImpl
import com.ice.domain.repositories.CredentialsRepo
import com.ice.domain.repositories.RemoteRepo
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class CredentialsModule {

    @Provides
    fun credentialsRepoImp(credentialsRepoImpl: CredentialsRepoImpl): CredentialsRepo {
        return credentialsRepoImpl
    }

}