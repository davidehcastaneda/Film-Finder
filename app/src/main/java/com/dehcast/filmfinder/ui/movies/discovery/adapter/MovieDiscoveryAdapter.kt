package com.dehcast.filmfinder.ui.movies.discovery.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dehcast.filmfinder.model.MoviePreview

class MovieDiscoveryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movies = mutableListOf<MoviePreview>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).bindModelToView(movies[position])
    }

    override fun getItemCount() = movies.size

    fun addMovies(newMovies: List<MoviePreview>) {
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}