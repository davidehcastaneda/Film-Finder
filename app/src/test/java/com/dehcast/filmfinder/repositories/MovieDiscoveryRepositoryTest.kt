package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.*
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MovieDiscoveryRepositoryTest {

    private lateinit var repository: MovieDiscoveryRepository

    @MockK
    private lateinit var movieDiscoveryApi: MovieDiscoveryApi

    @MockK
    private lateinit var configApi: MovieThumbnailConfigurationApi

    @MockK
    private lateinit var fakePageResponse: MoviePageResponse

    @MockK
    private lateinit var thumbnailConfig: ThumbnailConfiguration

    private var expectedUrl: String = ""
    private val notEmptyUrl = "https://fake"
    private val notEmptySize = "first poster size"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = MovieDiscoveryRepository(movieDiscoveryApi, configApi)
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
    fun `on Api success, fetchMoviePage returns Success`() = runBlocking {
        //Given
        givenMovieDiscoveryApiReturnsValidResponse()
        givenBaseUrlIsSetTo("path")
        val page = 1

        //When
        val response = repository.fetchMoviePage(page)

        //Then
        assert(response is NetworkResponse.Success)
    }

    @Test
    fun `on ApiSuccess, if baseUrl not set, thumbnailConfigApi is called`() = runBlocking {
        //Given
        givenBaseUrlIsEmpty()
        givenConfigApiReturnsValidConfig()
        givenMovieDiscoveryApiReturnsValidResponse()

        //When
        repository.fetchMoviePage(1)

        //Then
        coVerify(atLeast = 1) { configApi.fetchThumbnailConfiguration() }
    }

    @Test
    fun `getThumbnailUrlFrom() returns base secureUrl and posterSize from config if available`() {
        givenExpectedUrlIsComposedOf(notEmptyUrl, notEmptySize)
        givenConfigHasSecureBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf(notEmptySize))

        //When
        val response = repository.getThumbnailUrlFrom(thumbnailConfig)

        //Then
        assert(response == expectedUrl)
    }

    @Test
    fun `if no secureUrl getThumbnailUrlFrom(), uses baseUrl`() {
        givenExpectedUrlIsComposedOf(notEmptyUrl, notEmptySize)
        givenConfigHasSecureBaseUrlAs(null)
        givenConfigHasBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf(notEmptySize))

        //When
        val response = repository.getThumbnailUrlFrom(thumbnailConfig)

        //Then
        assert(response == expectedUrl)
    }

    @Test
    fun `getThumbnailUrlFrom() uses first not empty posterSize`() {
        givenExpectedUrlIsComposedOf(notEmptyUrl, notEmptySize)
        givenConfigHasSecureBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf("", "", notEmptySize))

        //When
        val response = repository.getThumbnailUrlFrom(thumbnailConfig)

        //Then
        assert(response == expectedUrl)
    }

    @Test
    fun `configureThumbnailBaseUrl() is not set if network exception happens`() = runBlocking {
        //Given
        givenBaseUrlIsEmpty()
        coEvery { configApi.fetchThumbnailConfiguration() }.throws(IllegalStateException())

        //When
        repository.configureThumbnailBaseUrl()

        //Then
        assert(repository.thumbnailBaseUrl?.isEmpty() == true)
    }

    @Test
    fun `configureThumbnailBaseUrl() is set with api parameters on response`() = runBlocking {
        givenConfigApiReturnsValidConfig()
        givenBaseUrlIsEmpty()

        //When
        repository.configureThumbnailBaseUrl()

        //Then
        assert(repository.thumbnailBaseUrl?.isEmpty() == false)
    }

    @Test
    fun `modifyPosterUrlForEachMovie adds baseurl to each movie poster posterPath`() {
        //Given
        val baseUrl = "Https://test"
        givenBaseUrlIsSetTo(baseUrl)
        val pageResponse = getPageResponseWithOneMovie()

        //When
        val response = repository.modifyPosterUrlForEachMovie(pageResponse)

        //Then
        assert(response.movies?.get(0)?.posterPath?.contains(baseUrl) == true)
    }

    private fun givenMovieDiscoveryApiReturnsValidResponse() {
        val pageResponse = getPageResponseWithOneMovie()
        coEvery { movieDiscoveryApi.fetchMoviePage(any()) }.returns(pageResponse)
    }

    private fun getPageResponseWithOneMovie(): MoviePageResponse = MoviePageResponse(
        movies = listOf(MoviePreview(posterPath = "path"))
    )

    private fun givenConfigApiReturnsValidConfig() {
        coEvery { configApi.fetchThumbnailConfiguration() }.returns(
            MovieThumbnailConfigurationResponse(thumbnailConfig)
        )
        givenConfigHasSecureBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf(notEmptySize))

    }

    private fun givenBaseUrlIsEmpty() {
        repository.thumbnailBaseUrl = ""
    }

    private fun givenBaseUrlIsSetTo(url: String?) {
        repository.thumbnailBaseUrl = url
    }

    private fun givenExpectedUrlIsComposedOf(url: String, size: String) {
        expectedUrl = url + size
    }

    private fun givenConfigHasSecureBaseUrlAs(url: String?) {
        every { thumbnailConfig.secureBaseUrl }.returns(url)
    }

    private fun givenConfigHasBaseUrlAs(url: String?) {
        every { thumbnailConfig.baseUrl }.returns(url)
    }

    private fun givenConfigHasPosterSizes(sizes: List<String>) {
        every { thumbnailConfig.posterSizes }.returns(sizes)
    }
}