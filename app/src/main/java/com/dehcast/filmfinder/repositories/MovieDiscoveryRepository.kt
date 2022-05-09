package com.dehcast.filmfinder.repositories

import androidx.annotation.VisibleForTesting
import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieGenreApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

interface MovieDiscoveryRepositoryContract {
    suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse>
}

class MovieDiscoveryRepository(
    private val movieDiscoveryApi: MovieDiscoveryApi,
    private val thumbnailConfigApi: MovieThumbnailConfigurationApi,
    private val movieGenreApi: MovieGenreApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MovieDiscoveryRepositoryContract {

    @VisibleForTesting
    var thumbnailBaseUrl: String? = null

    @VisibleForTesting
    var movieGenres: Hashtable<Int, String> = Hashtable()

    override suspend fun fetchMoviePage(page: Int): NetworkResponse<MoviePageResponse> =
        withContext(dispatcher) {
            try {
                var response = movieDiscoveryApi.fetchMoviePage(page)
                response = response.copy(movies = mapGenres(response.movies))
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

    @VisibleForTesting
    suspend fun mapGenres(movies: List<MoviePreview>?): List<MoviePreview>? {
        if (movieGenres.isEmpty) fetchMovieGenres()
        return movies?.map { movie -> setMainMovieGenreIfAvailable(movie) }
    }

    @VisibleForTesting
    suspend fun fetchMovieGenres() {
        try {
            movieGenreApi.fetchMovieGenres().genres?.forEach { genre ->
                addGenreIfNoParameterIsNull(genre)
            }
        } catch (_: Throwable) {
        }
    }

    private fun addGenreIfNoParameterIsNull(genre: Genre) {
        if (genre.id != null && genre.name != null) movieGenres[genre.id] = genre.name
    }

    private fun setMainMovieGenreIfAvailable(movie: MoviePreview): MoviePreview =
        if (movie.genreIds.isNullOrEmpty()) movie
        else {
            try {
                val mainGenre = movieGenres[movie.genreIds[0]]
                movie.copy(mainGenre = mainGenre)
            } catch (_: Throwable) {
                movie
            }
        }
}