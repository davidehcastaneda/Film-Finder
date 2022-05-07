package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [
        FragmentBuilderModule::class,
        ViewModelFactoryModule::class
    ])
    abstract fun contributeMainActivity(): MainActivity
}