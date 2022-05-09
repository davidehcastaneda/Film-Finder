package com.dehcast.filmfinder.repositories

import androidx.annotation.VisibleForTesting
import com.dehcast.filmfinder.apis.MovieDetailsApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.model.ThumbnailConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MovieDetailsRepositoryContract {
    suspend fun fetchFullMovie(movieId: Int): NetworkResponse<FullMovie>
}

class MovieDetailsRepository(
    private val movieDetailsApi: MovieDetailsApi,
    private val thumbnailConfigApi: MovieThumbnailConfigurationApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieDetailsRepositoryContract {

    @VisibleForTesting
    var thumbnailBaseUrl: String? = null

    override suspend fun fetchFullMovie(movieId: Int): NetworkResponse<FullMovie> =
        withContext(dispatcher) {
            try {
                val movie = movieDetailsApi.fetchMovieDetails(movieId)
                if (thumbnailBaseUrl.isNullOrEmpty()) configureThumbnailBaseUrl()
                NetworkResponse.Success(modifyPosterUrl(movie))

            } catch (e: Throwable) {
                NetworkResponse.Failure(e)
            }
        }

    private fun modifyPosterUrl(movie: FullMovie) =
        movie.copy(posterPath = thumbnailBaseUrl + movie.posterPath)

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
    fun getThumbnailUrlFrom(config: ThumbnailConfiguration): String {
        val baseUrl = config.secureBaseUrl ?: config.baseUrl
        val posterSizes = removePosterSizeIfMoreAvailable(config.posterSizes)
        return baseUrl + posterSizes.firstOrNull { it.isNotBlank() }
    }

    private fun removePosterSizeIfMoreAvailable(posterSizes: List<String>?): List<String> {
        return when {
            posterSizes.isNullOrEmpty() -> emptyList()
            posterSizes.size == 1 -> posterSizes
            else -> posterSizes.drop(1)
        }
    }
}