package com.dehcast.filmfinder.model

import com.google.gson.annotations.SerializedName

data class MovieThumbnailConfigurationResponse(
    @SerializedName("images")
    val thumbnailConfiguration: ThumbnailConfiguration,
)