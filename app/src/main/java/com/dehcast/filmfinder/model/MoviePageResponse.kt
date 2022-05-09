package com.dehcast.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MoviePageResponse(
    val page: Int? = null,
    @SerializedName("results")
    val movies: List<MoviePreview>? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
)
