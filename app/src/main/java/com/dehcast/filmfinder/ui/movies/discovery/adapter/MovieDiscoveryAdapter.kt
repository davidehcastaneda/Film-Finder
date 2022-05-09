package com.dehcast.filmfinder.ui.movies.discovery.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dehcast.filmfinder.model.MoviePreview

interface BottomReachedListener {

    fun onBottomReached()
}

class MovieDiscoveryAdapter(
    private val bottomReachedListener: BottomReachedListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movies = mutableListOf<MoviePreview>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bindModelToView(movies[position])
        if (isLastItem(position)) bottomReachedListener.onBottomReached()
    }

    private fun isLastItem(position: Int) = position == movies.size - 1

    override fun getItemCount() = movies.size

    fun addMovies(newMovies: List<MoviePreview>) {
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}