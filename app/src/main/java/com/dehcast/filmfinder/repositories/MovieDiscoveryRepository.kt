package com.dehcast.filmfinder.repositories

import androidx.annotation.VisibleForTesting
import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.MoviePageResponse
import com.dehcast.filmfinder.model.MoviePreview
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.model.ThumbnailConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MovieDiscoveryRepositoryContract {
    suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse>
}

class MovieDiscoveryRepository(
    private val movieDiscoveryApi: MovieDiscoveryApi,
    private val thumbnailConfigApi: MovieThumbnailConfigurationApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieDiscoveryRepositoryContract {

    @VisibleForTesting
    var thumbnailBaseUrl: String? = null

    override suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse> =
        withContext(dispatcher) {
            try {
                val response = movieDiscoveryApi.fetchMoviePage(page)
                if (thumbnailBaseUrl.isNullOrEmpty()) configureThumbnailBaseUrl()
                NetworkResponse.Success(modifyPosterUrlForEachMovie(response))
            } catch (e: Throwable) {
                NetworkResponse.Failure(e)
            }
        }

    @VisibleForTesting
    suspend fun configureThumbnailBaseUrl() {
        withContext(dispatcher) {
            try {
                thumbnailBaseUrl = getThumbnailUrlFrom(
                    thumbnailConfigApi.fetchThumbnailConfiguration().thumbnailConfiguration
                )
            } catch (e: Throwable) {
            }
        }
    }

    @VisibleForTesting
    fun modifyPosterUrlForEachMovie(moviePage: MoviePageResponse): MoviePageResponse {
        val modifiedResults: List<MoviePreview>? = moviePage.movies?.map { movie ->
            movie.copy(posterPath = thumbnailBaseUrl + movie.posterPath)
        }
        return moviePage.copy(movies = modifiedResults)
    }

    @VisibleForTesting
    fun getThumbnailUrlFrom(config: ThumbnailConfiguration): String {
        val baseUrl = config.secureBaseUrl ?: config.baseUrl
        val posterSizes = config.posterSizes ?: emptyList()
        return baseUrl + posterSizes.firstOrNull { it.isNotBlank() }
    }
}