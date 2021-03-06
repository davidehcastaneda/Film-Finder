package com.dehcast.filmfinder.apis

import com.dehcast.filmfinder.model.MoviePageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieDiscoveryApi {

    @GET("movie/popular")
    suspend fun fetchMoviePage(@Query("page") page: Int): MoviePageResponse
}