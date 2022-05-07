package com.dehcast.filmfinder.di.modules

import androidx.lifecycle.ViewModel
import com.dehcast.filmfinder.di.viewmodels.ViewModelKey
import com.dehcast.filmfinder.ui.movies.discovery.MovieDiscoveryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MovieDiscoveryViewModel::class)
    abstract fun provideMovieDiscoveryViewModel(viewModel: MovieDiscoveryViewModel): ViewModel
}