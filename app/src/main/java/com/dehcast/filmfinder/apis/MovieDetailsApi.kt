package com.dehcast.filmfinder.apis

import com.dehcast.filmfinder.model.FullMovie
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieDetailsApi {

    @GET("movie/{movieId}")
    suspend fun fetchMovieDetails(@Path("movieId") movieId: Int): FullMovie
}