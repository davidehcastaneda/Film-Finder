package com.dehcast.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MoviePageResponse(
    val page: Int?,
    val results: List<MoviePreview>?,
    @SerializedName("total_results")
    val totalResults: Int?,
    @SerializedName("total_pages")
    val totalPages: Int?,
)
