package com.dehcast.filmfinder.ui.movies.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dehcast.filmfinder.model.FullMovie
import com.dehcast.filmfinder.model.NetworkResponse
import com.dehcast.filmfinder.repositories.MovieDetailsRepositoryContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsViewModel @Inject constructor(
    private val movieDetailsRepo: MovieDetailsRepositoryContract,
) : ViewModel() {

    private val _state: MutableStateFlow<MovieDetailsState> =
        MutableStateFlow(MovieDetailsState.None)
    val state: StateFlow<MovieDetailsState> by lazy { _state }

    fun fetchMovieDetails(movieId: Int) {
        _state.value = MovieDetailsState.Loading
        viewModelScope.launch {
            when (val response = movieDetailsRepo.fetchFullMovie(movieId)) {
                is NetworkResponse.Failure -> onFailure()
                is NetworkResponse.Success -> onSuccess(response)
            }
        }
    }

    private fun onFailure() {
        _state.value = MovieDetailsState.Failure
    }

    private fun onSuccess(response: NetworkResponse.Success<FullMovie>) {
        _state.value = MovieDetailsState.Success(response.data)
    }

}