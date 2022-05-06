package com.dehcast.filmfinder.di.component

import android.app.Application
import com.dehcast.filmfinder.FilmFinderApplication
import com.dehcast.filmfinder.di.modules.ActivityBuilderModule
import com.dehcast.filmfinder.di.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBuilderModule::class,
        NetworkModule::class
    ]
)
interface AppComponent : AndroidInjector<FilmFinderApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}