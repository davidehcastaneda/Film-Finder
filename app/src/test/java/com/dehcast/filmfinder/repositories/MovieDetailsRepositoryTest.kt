package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDetailsApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.MovieThumbnailConfigurationResponse
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.model.ThumbnailConfiguration
import io.mockk.MockKAnnotations
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MovieDetailsRepositoryTest {

    @MockK
    private lateinit var movieDetailsApi: MovieDetailsApi

    @MockK
    private lateinit var thumbnailConfigApi: MovieThumbnailConfigurationApi

    private lateinit var repo: MovieDetailsRepository

    private var networkResponse: NetworkResponse<FullMovie>? = null

    private val defaultThumbnailPath = "https://path"
    private val baseThumbnailPath = "/google"

    private val fakeMovie = FullMovie(posterPath = defaultThumbnailPath)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repo = MovieDetailsRepository(movieDetailsApi, thumbnailConfigApi)
    }

    @Test
    fun `on network exception, fetchMovieDetails returns Failure`() = runBlocking {
        givenNetworkException()

        whenFetchFullMovie()

        //Then
        assert(networkResponse is NetworkResponse.Failure)
    }

    @Test
    fun `id api success, fetchMovieDetails returns network success`() = runBlocking {
        givenMovieDetailsApiReturnsMovie()
        givenThereIsThumbnailConfig()

        whenFetchFullMovie()

        //Then
        assert(networkResponse is NetworkResponse.Success)
    }

    @Test
    fun `if avilableThumbnail, thumbnail api not called`() = runBlocking {
        givenMovieDetailsApiReturnsMovie()
        givenThereIsThumbnailConfig()

        whenFetchFullMovie()

        //Then
        coVerify { thumbnailConfigApi.fetchThumbnailConfiguration() wasNot called }
    }

    @Test
    fun `base thumbnail path is added to posterUrl `() = runBlocking {
        givenMovieDetailsApiReturnsMovie()
        givenThereIsThumbnailConfig()

        whenFetchFullMovie()

        //Then
        assert(
            (networkResponse as NetworkResponse.Success)
                .data.posterPath?.contains(baseThumbnailPath) == true
        )
    }

    @Test
    fun `if available, second smallest poster path will be chosen`() = runBlocking {
        givenNoThumbnailConfig()
        givenMovieDetailsApiReturnsMovie()
        givenThumbnailApiReturnsThreeValidSizes()

        whenFetchFullMovie()

        //Then
        assert(
            (networkResponse as NetworkResponse.Success)
                .data.posterPath?.contains(baseThumbnailPath) == true
        )
    }

    private suspend fun givenThumbnailApiReturnsThreeValidSizes() {
        coEvery { thumbnailConfigApi.fetchThumbnailConfiguration() }.returns(
            MovieThumbnailConfigurationResponse(
                ThumbnailConfiguration(
                    posterSizes = listOf("A", baseThumbnailPath, "A"),
                    baseUrl = "https://"
                )))
    }

    private fun givenMovieDetailsApiReturnsMovie() {
        coEvery { movieDetailsApi.fetchMovieDetails(any()) }.returns(fakeMovie)
    }

    private fun givenNetworkException() {
        coEvery { movieDetailsApi.fetchMovieDetails(any()) }.throws(IllegalStateException())
    }

    private suspend fun whenFetchFullMovie() {
        networkResponse = repo.fetchFullMovie(1)
    }

    private fun givenThereIsThumbnailConfig() {
        repo.thumbnailBaseUrl = baseThumbnailPath
    }

    private fun givenNoThumbnailConfig() {
        repo.thumbnailBaseUrl = null
    }

}