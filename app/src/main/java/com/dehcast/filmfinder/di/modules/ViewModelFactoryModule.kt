package com.dehcast.filmfinder.di.modules

import androidx.lifecycle.ViewModelProvider
import com.dehcast.filmfinder.di.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    internal abstract fun provideViewModelFactoryModule(factory: ViewModelFactory): ViewModelProvider.Factory
}