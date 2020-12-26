package com.ice.covidalert

import android.app.Application
import android.content.Context
import com.ice.covidalert.di.components.AppComponent
import com.ice.covidalert.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class CovidApplication: DaggerApplication() {

    companion object {
        lateinit var instance: CovidApplication private set
    }
    private lateinit var appComponent: AppComponent

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
//        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

//        initializeAppComponent(instance)
//        appComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        return appComponent
    }
}