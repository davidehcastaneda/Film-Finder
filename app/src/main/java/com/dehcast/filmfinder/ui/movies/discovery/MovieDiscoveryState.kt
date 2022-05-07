package com.dehcast.filmfinder.ui.movies.discovery

import com.dehcast.filmfinder.model.MoviePreview

sealed class MovieDiscoveryState {
    object None : MovieDiscoveryState()
    object Loading : MovieDiscoveryState()
    object Failure : MovieDiscoveryState()
    data class Success(val movies: List<MoviePreview>) : MovieDiscoveryState()
}