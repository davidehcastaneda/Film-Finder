package com.dehcast.filmfinder.di.modules

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieGenreApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepository
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepositoryContract
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieDiscoveryRepository(
        movieDiscoveryApi: MovieDiscoveryApi,
        thumbnailConfigurationApi: MovieThumbnailConfigurationApi,
        movieGenreApi: MovieGenreApi,
    ): MovieDiscoveryRepositoryContract =
        MovieDiscoveryRepository(movieDiscoveryApi, thumbnailConfigurationApi, movieGenreApi)
}