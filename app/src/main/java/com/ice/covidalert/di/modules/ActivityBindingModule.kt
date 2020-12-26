package com.ice.covidalert.di.modules

import com.ice.covidalert.ui.main.LoginActivity
import com.ice.covidalert.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    internal abstract fun bindMainScreenActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun bindLoginScreenActivity(): LoginActivity
}