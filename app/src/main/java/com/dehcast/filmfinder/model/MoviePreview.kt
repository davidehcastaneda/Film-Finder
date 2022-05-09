package com.dehcast.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MoviePreview(
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("adult")
    val isAdultMovie: Boolean? = null,
    val overview: String? = null,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,
    val id: Int? = null,
    @SerializedName("original_title")
    val originalTitle: String? = null,
    @SerializedName("original_language")
    val originalLanguage: String? = null,
    val title: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    val popularity: Double? = null,
    @SerializedName("vote_count")
    val voteCount: Int? = null,
    val video: Boolean? = null,
    @SerializedName("vote_average")
    val voteAverage: Double? = null,
    val mainGenre: String? = null,
)