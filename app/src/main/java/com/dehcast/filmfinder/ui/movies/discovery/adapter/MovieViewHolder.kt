package com.dehcast.filmfinder.ui.movies.discovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.RowItemMovieBinding
import com.dehcast.filmfinder.model.MoviePreview
import com.dehcast.filmfinder.utils.getJustYear
import com.dehcast.filmfinder.utils.hideIfPropertyIsNullOrResolveWithProperty
import com.dehcast.filmfinder.utils.toStringWithNoDecimals


class MovieViewHolder(
    private val parent: ViewGroup,
    private val binding: RowItemMovieBinding = RowItemMovieBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    ),
) : RecyclerView.ViewHolder(binding.root) {

    fun bindModelToView(model: MoviePreview) {
        setThumbnail(model.posterPath)
        setTextOrHideIfNoData(binding.title, model.title)
        setTextOrHideIfNoData(
            binding.popularity,
            itemView.context.getString(R.string.popularity_rate,
                model.voteAverage?.toStringWithNoDecimals())
        )
        setTextOrHideIfNoData(binding.releaseYear, model.releaseDate?.getJustYear())
    }

    private fun setThumbnail(posterPath: String?) {
        binding.thumbnail.hideIfPropertyIsNullOrResolveWithProperty(posterPath) { urlPath ->
            Glide.with(binding.root.context)
                .load(urlPath as String)
                .into(binding.thumbnail)
        }
    }

    private fun setTextOrHideIfNoData(view: AppCompatTextView, text: String?) {
        view.hideIfPropertyIsNullOrResolveWithProperty(text) { notNullText ->
            view.text = notNullText as String
        }
    }
}