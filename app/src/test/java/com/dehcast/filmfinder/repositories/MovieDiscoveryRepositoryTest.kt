package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.model.MoviePageResponse
import com.dehcast.filmfinder.model.NetworkResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MovieDiscoveryRepositoryTest {

    private lateinit var repository: MovieDiscoveryRepository

    @MockK
    private lateinit var movieDiscoveryApi: MovieDiscoveryApi

    @MockK
    private lateinit var fakePageResponse: MoviePageResponse

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = MovieDiscoveryRepository(movieDiscoveryApi)
    }

    @Test
    fun `on Api exception, fetchMoviePage returns Failure`() = runBlocking {
        //Given
        val page = 0
        coEvery { movieDiscoveryApi.fetchMoviePage(page) }.throws(IllegalStateException())

        //When
        val response = repository.fetchMoviePage(page)

        //Then
        assert(response is NetworkResponse.Failure)
    }

    @Test
    fun `on Api success, fetchMoviePage returns Success with data`() = runBlocking {
        //Given
        val page = 0
        coEvery { movieDiscoveryApi.fetchMoviePage(page) }.returns(fakePageResponse)

        //When
        val response = repository.fetchMoviePage(page)

        //Then
        assert(response is NetworkResponse.Success)
        assert((response as NetworkResponse.Success).data == fakePageResponse)
    }
}