package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDetailsApi
import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.NetworkResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MovieDetailsRepositoryTest {

    @MockK
    private lateinit var movieDetailsApi: MovieDetailsApi

    @MockK
    private lateinit var fakeMovie: FullMovie

    private lateinit var repo: MovieDetailsRepository

    private var networkResponse: NetworkResponse<FullMovie>? = null

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repo = MovieDetailsRepository(movieDetailsApi)
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

        whenFetchFullMovie()

        //Then
        assert(networkResponse is NetworkResponse.Success)
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

}