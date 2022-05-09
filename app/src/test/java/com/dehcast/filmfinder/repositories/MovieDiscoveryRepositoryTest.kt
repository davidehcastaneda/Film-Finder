package com.dehcast.filmfinder.repositories

import com.dehcast.filmfinder.apis.MovieDiscoveryApi
import com.dehcast.filmfinder.apis.MovieGenreApi
import com.dehcast.filmfinder.apis.MovieThumbnailConfigurationApi
import com.dehcast.filmfinder.model.*
import io.mockk.*
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
    private lateinit var genreApi: MovieGenreApi

    @MockK
    private lateinit var fakePageResponse: MoviePageResponse

    @MockK
    private lateinit var thumbnailConfig: ThumbnailConfiguration

    private var expectedUrl: String = ""
    private val notEmptyUrl = "https://fake"
    private val notEmptySize = "first poster size"
    private val defaultGenreId = 1000
    private val defaultGenreName = "Action"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = MovieDiscoveryRepository(movieDiscoveryApi, configApi, genreApi)
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
    fun `getThumbnailUrlFrom() uses first not empty posterSize if secondOption not available`() {
        givenExpectedUrlIsComposedOf(notEmptyUrl, notEmptySize)
        givenConfigHasSecureBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf("", "", notEmptySize))

        //When
        val response = repository.getThumbnailUrlFrom(thumbnailConfig)

        //Then
        assert(response == expectedUrl)
    }

    @Test
    fun `getThumbnailUrlFrom() uses second posterSize if not empty`() {
        givenExpectedUrlIsComposedOf(notEmptyUrl, notEmptySize)
        givenConfigHasSecureBaseUrlAs(notEmptyUrl)
        givenConfigHasPosterSizes(listOf("A", notEmptySize, "A"))

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

    @Test
    fun `on empty movie genres, movieGenreApi is called`() = runBlocking {
        givenMovieGenresEmpty()
        givenConfigApiReturnsValidConfig()
        givenMovieDiscoveryApiReturnsValidResponse()

        //When
        repository.fetchMovieGenres()

        //Then
        coVerify(atLeast = 1) { genreApi.fetchMovieGenres() }
    }

    @Test
    fun `on movie genres not empty, movieGenreApi is not called`() = runBlocking {
        givenMovieGenresNotEmpty()
        givenConfigApiReturnsValidConfig()
        givenMovieDiscoveryApiReturnsValidResponse()
        //When
        repository.fetchMovieGenres()

        //Then
        coVerify { genreApi.fetchMovieGenres() wasNot called }
    }

    @Test
    fun `if no movieGenres and genreApi gives valid response, movieGenres get populated`() =
        runBlocking {
            givenMovieGenresEmpty()
            givenGenreApiGivesValidList()
            givenConfigApiReturnsValidConfig()
            givenMovieDiscoveryApiReturnsValidResponse()

            //When
            repository.fetchMovieGenres()

            //Then
            assert(true)
            assert(repository.movieGenres[defaultGenreId] == defaultGenreName)
        }

    @Test
    fun `mainGenre populated if movieGenres available`() = runBlocking {
        givenMovieGenresEmpty()
        givenGenreApiGivesValidList()
        givenConfigApiReturnsValidConfig()
        givenMovieDiscoveryApiReturnsValidResponse()

        //When
        val response = repository.fetchMoviePage(1)

        //Then
        assert((response as NetworkResponse.Success).data.movies?.get(0)?.mainGenre == defaultGenreName)
    }

    private fun givenMovieGenresNotEmpty() {
        repository.movieGenres[defaultGenreId] = defaultGenreName
    }

    private fun givenMovieGenresEmpty() {
        repository.movieGenres.clear()
    }

    private fun givenGenreApiGivesValidList() {
        coEvery { genreApi.fetchMovieGenres() }.returns(
            GenreResponse(
                genres = listOf(Genre(defaultGenreId, defaultGenreName))
            )
        )
    }

    private fun givenMovieDiscoveryApiReturnsValidResponse() {
        val pageResponse = getPageResponseWithOneMovie()
        coEvery { movieDiscoveryApi.fetchMoviePage(any()) }.returns(pageResponse)
    }

    private fun getPageResponseWithOneMovie(): MoviePageResponse = MoviePageResponse(
        movies = listOf(MoviePreview(posterPath = "path", genreIds = listOf(defaultGenreId)))
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