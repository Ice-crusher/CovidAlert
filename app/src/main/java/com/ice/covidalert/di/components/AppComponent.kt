package com.ice.covidalert.di.components

import android.app.Application
import com.ice.covidalert.CovidApplication
import com.ice.covidalert.di.modules.ActivityBindingModule
import com.ice.covidalert.di.modules.AppModule
import com.ice.covidalert.di.modules.ServiceBindingModule
import com.ice.data.di.ApiModule
import com.ice.data.di.CredentialsModule
import com.ice.data.di.NetworkModule
import com.ice.data.di.RemoteModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ApiModule::class,
        AppModule::class,
        RemoteModule::class,
        CredentialsModule::class,
        ActivityBindingModule::class,
        ServiceBindingModule::class,
        AndroidInjectionModule::class
    ]
)
interface AppComponent: AndroidInjector<CovidApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}