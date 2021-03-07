package com.ice.covidalert.di.modules

import com.ice.covidalert.services.FCMService
import com.ice.covidalert.services.NearbyService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBindingModule {

    @ContributesAndroidInjector
    abstract fun bindNearbyService(): NearbyService

    @ContributesAndroidInjector
    abstract fun bindFCMService(): FCMService
}