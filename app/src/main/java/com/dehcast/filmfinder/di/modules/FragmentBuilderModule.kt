package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.ui.movies.details.MovieDetailsFragment
import com.dehcast.filmfinder.ui.movies.discovery.MovieDiscoveryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun provideMovieDiscoveryFragment(): MovieDiscoveryFragment

    @ContributesAndroidInjector
    abstract fun provideMovieDetailsFragment(): MovieDetailsFragment
}