package com.dehcast.filmfinder.apis

import com.dehcast.filmfinder.model.GenreResponse
import retrofit2.http.GET

interface MovieGenreApi {

    @GET("genre/movie/list")
    suspend fun fetchMovieGenres(): GenreResponse
}