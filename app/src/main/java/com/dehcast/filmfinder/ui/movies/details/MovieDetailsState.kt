package com.dehcast.filmfinder.ui.movies.details

import com.dehcast.filmfinder.model.FullMovie

sealed class MovieDetailsState {
    object None : MovieDetailsState()
    object Failure : MovieDetailsState()
    object Loading : MovieDetailsState()
    data class Success(
        val data: FullMovie,
    ) : MovieDetailsState()
}