package com.dehcast.filmfinder.ui.movies.discovery

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dehcast.filmfinder.model.MoviePageResponse
import com.dehcast.filmfinder.model.MoviePreview
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.repositories.MovieDiscoveryRepositoryContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDiscoveryViewModel @Inject constructor(
    private val movieDiscoveryRepo: MovieDiscoveryRepositoryContract,
) : ViewModel() {

    private val _state: MutableStateFlow<MovieDiscoveryState> by lazy {
        MutableStateFlow(MovieDiscoveryState.None)
    }

    private val cachedMovies = mutableListOf<MoviePreview>()

    val state: StateFlow<MovieDiscoveryState> by lazy { _state }

    @VisibleForTesting
    var currentPage = 1

    @VisibleForTesting
    var canQueryMore = true

    fun fetchMoviePage() {
        if ((state.value is MovieDiscoveryState.Loading) || !canQueryMore) return
        _state.value = MovieDiscoveryState.Loading

        viewModelScope.launch {
            when (val response = movieDiscoveryRepo.fetchMoviePage(currentPage)) {
                is NetworkResponse.Failure -> handlePageError()
                is NetworkResponse.Success -> handlePageFetched(response)
            }
        }

    }

    @VisibleForTesting
    fun handlePageError() {
        _state.value = MovieDiscoveryState.Failure
    }

    @VisibleForTesting
    fun handlePageFetched(response: NetworkResponse.Success<MoviePageResponse>) {
        checkIfCanStillQueryMore(response.data.totalPages)
        val results = response.data.movies
        if (results.isNullOrEmpty()) _state.value = MovieDiscoveryState.Failure
        else cacheAndPublishMovies(results)
    }

    private fun cacheAndPublishMovies(results: List<MoviePreview>) {
        cachedMovies.addAll(results)
        _state.value = (MovieDiscoveryState.Success(cachedMovies))
    }

    @VisibleForTesting
    fun checkIfCanStillQueryMore(availablePages: Int?) {
        currentPage++
        availablePages?.let { maxPages ->
            canQueryMore = canQueryMore && currentPage < maxPages
        }
    }

}