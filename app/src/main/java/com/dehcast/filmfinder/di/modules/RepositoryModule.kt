package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepository
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieDiscoveryRepository(movieDiscoveryApi: MovieDiscoveryApi): MovieDiscoveryRepositoryContract =
        MovieDiscoveryRepository(movieDiscoveryApi)
}