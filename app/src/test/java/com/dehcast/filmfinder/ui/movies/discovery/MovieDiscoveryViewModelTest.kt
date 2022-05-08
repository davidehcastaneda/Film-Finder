package com.dehcast.filmfinder.ui.movies.discovery

import com.dehcast.filmfinder.model.MoviePageResponse
import com.dehcast.filmfinder.model.MoviePreview
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDiscoveryViewModelTest {

    private val repo = mockk<MovieDiscoveryRepository>(relaxed = true)
    private val viewmodel = spyk(MovieDiscoveryViewModel(repo))

    @MockK
    private lateinit var FAKE_PAGE_RESPONSE: MoviePageResponse

    @MockK
    private lateinit var FAILURE: NetworkResponse.Failure

    @MockK
    private lateinit var SUCCESS: NetworkResponse.Success<MoviePageResponse>

    @MockK
    private lateinit var FAKE_MOVIES: List<MoviePreview>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `on state loading, fetchMoviePage doesn't fetch data`() {
        givenCanQueryMoreTrue()
        givenStateLoading()

        whenFetchMoviePage()

        //Then
        coVerify { repo.fetchMoviePage(any()) wasNot called }
    }

    @Test
    fun `on flag canQueryMore false, fetchMoviePage doesn't fetch data`() {
        givenFetchPageFails()
        givenCanQueryMoreFalse()
        givenStateNotLoading()

        whenFetchMoviePage()

        //Then
        coVerify { repo.fetchMoviePage(any()) wasNot called }
    }

    @Test
    fun `if state not loading and can query more, data is asked for`() {
        givenStateNotLoading()
        givenCanQueryMoreTrue()

        whenFetchMoviePage()

        //Then
        coVerify(atLeast = 1) { repo.fetchMoviePage(any()) }
    }

    @Test
    fun `if repo returns failure on fetchMoviePage, failure state is published`() {
        givenCanQueryMoreTrue()
        givenFetchPageFails()

        whenFetchMoviePage()

        //Then
        assert(viewmodel.state.value is MovieDiscoveryState.Failure)
    }

    @Test
    fun `if repo returns success on fetchMoviePage, success state is published`() {
        givenCanQueryMoreTrue()
        givenFetchPageSucceeds()

        whenFetchMoviePage()

        //Then
        val state = viewmodel.state.value
        assert(state is MovieDiscoveryState.Success)
        assert((state as MovieDiscoveryState.Success).movies == FAKE_MOVIES)
    }

    @Test
    fun `if Movie page has no movies, failure state is published`() {
        givenSuccessHasNOMovies()

        //When
        viewmodel.handlePageFetched(SUCCESS)

        //Then
        assert(viewmodel.state.value is MovieDiscoveryState.Failure)
    }

    @Test
    fun `if Movie page has NULL movies, failure state is published`() {
        givenSuccessHasNULLMovies()

        //When
        viewmodel.handlePageFetched(SUCCESS)

        //Then
        assert(viewmodel.state.value is MovieDiscoveryState.Failure)
    }

    private fun whenFetchMoviePage() {
        viewmodel.fetchMoviePage()
    }

    private fun givenFetchPageFails() {
        coEvery { repo.fetchMoviePage(any()) }.returns(FAILURE)
    }

    private fun givenFetchPageSucceeds() {
        coEvery { repo.fetchMoviePage(any()) }.returns(SUCCESS)
        givenSuccessHasData()
    }

    private fun givenSuccessHasData() {
        givenPageWithLotsOfPages()
        every { FAKE_PAGE_RESPONSE.results }.returns(FAKE_MOVIES)
        every { FAKE_MOVIES.isEmpty() }.returns(false)
    }

    private fun givenSuccessHasNULLMovies() {
        givenPageWithLotsOfPages()
        every { FAKE_PAGE_RESPONSE.results }.returns(null)
    }

    private fun givenSuccessHasNOMovies() {
        givenPageWithLotsOfPages()
        every { FAKE_PAGE_RESPONSE.results }.returns(emptyList())
        every { FAKE_MOVIES.isEmpty() }.returns(true)
    }

    private fun givenPageWithLotsOfPages() {
        every { SUCCESS.data }.returns(FAKE_PAGE_RESPONSE)
        every { FAKE_PAGE_RESPONSE.totalPages }.returns(Int.MAX_VALUE)
    }

    private fun givenCanQueryMoreTrue() {
        every { viewmodel.canQueryMore }.returns(true)
    }

    private fun givenCanQueryMoreFalse() {
        every { viewmodel.canQueryMore }.returns(false)
    }

    private fun givenStateLoading() {
        every { viewmodel.state.value }.returns(MovieDiscoveryState.Loading)
    }

    private fun givenStateNotLoading() {
        every { viewmodel.state.value }.returns(MovieDiscoveryState.None)
    }

}