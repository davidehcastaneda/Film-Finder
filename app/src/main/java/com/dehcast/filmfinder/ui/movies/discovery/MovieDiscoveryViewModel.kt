package com.dehcast.filmfinder.ui.movies.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dehcast.filmfinder.model.MoviePageResponse
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

    val state: StateFlow<MovieDiscoveryState> by lazy { _state }
    private var currentPage = 1
    private var canQueryMore = true

    fun fetchMoviePage() {
        if (state.value == MovieDiscoveryState.Loading || !canQueryMore) return
        _state.value = MovieDiscoveryState.Loading

        viewModelScope.launch {
            when (val response = movieDiscoveryRepo.fetchMoviePage(currentPage)) {
                is NetworkResponse.Failure -> handlePageError(response)
                is NetworkResponse.Success -> handlePageFetched(response)
            }
        }

    }

    private fun handlePageError(response: NetworkResponse.Failure) {
        _state.value = MovieDiscoveryState.Failure
    }

    private fun handlePageFetched(response: NetworkResponse.Success<MoviePageResponse>) {
        updateControllingParameters(response.data.totalPages)
        val results = response.data.results
        _state.value =
            if (results.isNullOrEmpty()) MovieDiscoveryState.Failure
            else MovieDiscoveryState.Success(results)
    }

    private fun updateControllingParameters(availablePages: Int?) {
        availablePages?.let { maxPages ->
            canQueryMore = canQueryMore && currentPage < maxPages
        }
        currentPage++
    }

}