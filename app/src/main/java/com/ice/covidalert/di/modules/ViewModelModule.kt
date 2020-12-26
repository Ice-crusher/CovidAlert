package com.ice.covidalert.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ice.covidalert.di.ViewModelFactory
import com.ice.covidalert.di.ViewModelKey
import com.ice.covidalert.viewmodel.LoginViewModel
import com.ice.covidalert.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindingLoginViewModel(viewModel: LoginViewModel): ViewModel


}