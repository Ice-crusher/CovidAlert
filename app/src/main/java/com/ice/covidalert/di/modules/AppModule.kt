package com.ice.covidalert.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ice.covidalert.di.ActivityScope
import com.ice.covidalert.rx.SchedulersFacade
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.data.preferences.PreferenceHelper
import com.ice.data.preferences.PreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun bindContext(application: Application): Context = application

    @Provides
    fun providerScheduler(schedulersFacade: SchedulersFacade): SchedulersProvider = schedulersFacade

    @Provides
    fun preferences(preferenceImpl: PreferenceImpl): PreferenceHelper = preferenceImpl

    @Singleton
    @Provides
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("covid_pref", Context.MODE_PRIVATE)
    }

}