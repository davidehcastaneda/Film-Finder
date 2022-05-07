package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.model.MoviePageResponse
import com.dehcast.filmfinder.model.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MovieDiscoveryRepositoryContract {
    suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse>
}

class MovieDiscoveryRepository(
    private val movieDiscoveryApi: MovieDiscoveryApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieDiscoveryRepositoryContract {

    override suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse> =
        withContext(dispatcher) {
            try {
                NetworkResponse.Success(movieDiscoveryApi.fetchMoviePage(page))
            } catch (e: Throwable) {
                NetworkResponse.Failure(e)
            }
        }
}