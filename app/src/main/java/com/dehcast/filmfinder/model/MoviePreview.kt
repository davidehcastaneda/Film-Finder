package com.dehcast.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MoviePreview(
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("adult")
    val isAdultMovie: Boolean?,
    val overview: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("genre_ids")
    val genreIds: List<Int>?,
    val id: Int?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    val title: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val popularity: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    val video: Boolean?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
)