package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDetailsApi
import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MovieDetailsRepositoryContract {
    suspend fun fetchFullMovie(movieId: Int): NetworkResponse<FullMovie>
}

class MovieDetailsRepository(
    private val movieDetailsApi: MovieDetailsApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieDetailsRepositoryContract {

    override suspend fun fetchFullMovie(movieId: Int): NetworkResponse<FullMovie> =
        withContext(dispatcher) {
            try {
                NetworkResponse.Success(movieDetailsApi.fetchMovieDetails(movieId))
            } catch (e: Throwable) {
                NetworkResponse.Failure(e)
            }
        }
}