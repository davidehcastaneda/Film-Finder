package com.dehcast.filmfinder.ui.movies.discovery.adapter.diffutils

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dehcast.filmfinder.model.MoviePreview

class MovieDiscoveryDiffUtils {

    fun dispatchDiffs(
        originalMovies: List<MoviePreview>,
        targetMovies: List<MoviePreview>,
        targetAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    ) {

        val diffs = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = originalMovies.size

            override fun getNewListSize() = targetMovies.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                originalMovies[oldItemPosition] == targetMovies[newItemPosition]


            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                originalMovies[oldItemPosition].id == originalMovies[newItemPosition].id

        })

        diffs.dispatchUpdatesTo(targetAdapter)
    }
}