package com.dehcast.filmfinder.ui.movies.discovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.RowItemMovieBinding
import com.dehcast.filmfinder.model.MoviePreview
import com.dehcast.filmfinder.ui.movies.discovery.MovieDiscoveryFragmentDirections
import com.dehcast.filmfinder.utils.getJustYear
import com.dehcast.filmfinder.utils.hideIfPropertyIsNullOrResolveWithProperty
import com.dehcast.filmfinder.utils.setTextOrHideViewIfNoData
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
        model.title.setTextOrHideViewIfNoData(binding.title)
        model.mainGenre.setTextOrHideViewIfNoData(binding.genre)
        model.releaseDate?.getJustYear().setTextOrHideViewIfNoData(binding.releaseYear)
        setMovieSelectedClickListener(model.id)

        itemView.context.getString(
            R.string.popularity_rate,
            model.voteAverage?.toStringWithNoDecimals()
        ).setTextOrHideViewIfNoData(binding.popularity)
    }

    private fun setMovieSelectedClickListener(movieId: Int?) {
        binding.root.setOnClickListener {
            if (movieId == null) toastNoMovieIdError()
            else navigateToMovieDetailsFragment(movieId)
        }
    }

    private fun navigateToMovieDetailsFragment(movieId: Int) {
        Navigation.findNavController(binding.root).navigate(
            MovieDiscoveryFragmentDirections.actionMovieDiscoveryFragmentToMovieDetailsFragment(
                movieId
            )
        )
    }

    private fun toastNoMovieIdError() {
        Toast.makeText(
            itemView.context,
            itemView.context.getString(R.string.movie_with_no_id_onclick_error_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setThumbnail(posterPath: String?) {
        binding.thumbnail.hideIfPropertyIsNullOrResolveWithProperty(posterPath) { urlPath ->
            Glide.with(binding.root.context)
                .load(urlPath as String)
                .into(binding.thumbnail)
        }
    }
}