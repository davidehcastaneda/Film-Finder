package com.dehcast.filmfinder.ui.movies.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.FragmentMovieDetailsBinding
import com.dehcast.filmfinder.utils.getJustYear
import com.dehcast.filmfinder.utils.hideIfPropertyIsNullOrResolveWithProperty
import com.dehcast.filmfinder.utils.setTextOrHideViewIfNoData
import com.dehcast.filmfinder.utils.toStringWithNoDecimals
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsFragment : DaggerFragment() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private val movieDetailsViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[MovieDetailsViewModel::class.java]
    }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding: FragmentMovieDetailsBinding get() = _binding!!
    private val args: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        fetchMovieDetails()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                movieDetailsViewModel.state.collectLatest { state ->
                    onStateChanged(state)
                }
            }
        }
    }

    private fun onStateChanged(state: MovieDetailsState) {
        when (state) {
            is MovieDetailsState.Loading -> onLoadingState()
            MovieDetailsState.Failure -> onFailureState()
            is MovieDetailsState.Success -> onSuccessState(state)
        }
    }

    private fun onLoadingState() {
        binding.shimmer.visibility = View.VISIBLE
        binding.content.visibility = View.GONE
    }

    private fun onFailureState() {
        binding.shimmer.visibility = View.GONE
        binding.content.visibility = View.GONE
        Toast.makeText(context, getString(R.string.no_movie_details_found), Toast.LENGTH_LONG)
            .show()
    }

    private fun onSuccessState(state: MovieDetailsState.Success) {
        binding.shimmer.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
        with(state.data) {
            title.setTextOrHideViewIfNoData(binding.title)
            voteAverage?.toStringWithNoDecimals()
                .setTextOrHideViewIfNoData(binding.popularity, R.string.popularity_template)
            overview.setTextOrHideViewIfNoData(binding.description)
            setHomePageLink(homepage)
            releaseDate?.getJustYear()
                .setTextOrHideViewIfNoData(binding.releaseYear, R.string.year_template)
            runtime?.toString()
                ?.setTextOrHideViewIfNoData(binding.runtime, R.string.runtime_template)
            genres?.firstOrNull { it.name != null }?.name.setTextOrHideViewIfNoData(binding.genre,
                R.string.genre_template)
            setThumbnail(posterPath)
        }
    }

    private fun setHomePageLink(homepage: String?) {
        with(binding.visitSiteButton) {
            if (homepage == null) visibility = View.GONE
            else {
                visibility = View.VISIBLE
                setOnClickListener { navigateToUrl(homepage) }
            }
        }
    }

    private fun navigateToUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    private fun setThumbnail(posterPath: String?) {
        binding.thumbnail.hideIfPropertyIsNullOrResolveWithProperty(posterPath) { urlPath ->
            Glide.with(requireContext())
                .load(urlPath as String)
                .into(binding.thumbnail)
        }
    }

    private fun fetchMovieDetails() {
        movieDetailsViewModel.fetchMovieDetails(args.movieId)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}