package com.dehcast.filmfinder.apis

import com.dehcast.filmfinder.model.MovieThumbnailConfigurationResponse
import retrofit2.http.GET

interface MovieThumbnailConfigurationApi {

    @GET("configuration")
    suspend fun fetchThumbnailConfiguration(): MovieThumbnailConfigurationResponse
}