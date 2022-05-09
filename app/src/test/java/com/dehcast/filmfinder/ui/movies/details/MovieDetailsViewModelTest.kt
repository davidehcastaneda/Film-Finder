package com.dehcast.filmfinder.ui.movies.details

import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.repositories.MovieDetailsRepositoryContract
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDetailsViewModelTest {

    private val repo = mockk<MovieDetailsRepositoryContract>(relaxed = true)
    private val viewmodel = spyk(MovieDetailsViewModel(repo))

    @MockK
    private lateinit var FAILURE: NetworkResponse.Failure

    @MockK
    private lateinit var SUCCESS: NetworkResponse.Success<FullMovie>

    @MockK
    private lateinit var FAKE_MOVIE: FullMovie

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `if repo returns failure, failure state is published`() = runBlocking {
        givenRepoReturns(FAILURE)

        whenFetchMovieDetailsCalled()

        thenStateIs(MovieDetailsState.Failure)
    }

    @Test
    fun `if repo returns success, failure state is success`() = runBlocking {
        givenRepoReturns(SUCCESS)
        givenSUCCESSHasData()

        whenFetchMovieDetailsCalled()

        thenStateIs(MovieDetailsState.Success(FAKE_MOVIE))
    }

    private fun givenSUCCESSHasData() {
        every { SUCCESS.data }.returns(FAKE_MOVIE)
    }

    private suspend fun givenRepoReturns(response: NetworkResponse<FullMovie>) {
        coEvery { repo.fetchFullMovie(any()) }.returns(response)
    }

    private fun whenFetchMovieDetailsCalled() {
        viewmodel.fetchMovieDetails(1)
    }

    private fun thenStateIs(expected: MovieDetailsState) {
        assert(viewmodel.state.value.javaClass == expected::class.java)
    }
}