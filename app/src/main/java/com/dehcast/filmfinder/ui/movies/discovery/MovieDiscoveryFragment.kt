package com.dehcast.filmfinder.ui.movies.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.FragmentMovieDiscoveryBinding
import com.dehcast.filmfinder.ui.movies.discovery.adapter.BottomReachedListener
import com.dehcast.filmfinder.ui.movies.discovery.adapter.MovieDiscoveryAdapter
import com.dehcast.filmfinder.utils.getDpAsFloat
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDiscoveryFragment : DaggerFragment(), BottomReachedListener {

    private var _binding: FragmentMovieDiscoveryBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding: FragmentMovieDiscoveryBinding get() = _binding!!

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private val movieDiscoveryViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[MovieDiscoveryViewModel::class.java]
    }

    private val movieAdapter by lazy { MovieDiscoveryAdapter(this) }
    private var recyclerColumns = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMovieRecycler()
        observeViewModel()
        if (movieDiscoveryViewModel.state.value is MovieDiscoveryState.None) fetchMovies()
    }

    private fun setupMovieRecycler() {
        updateRecyclerColumnsBasedOffOrientation()
        with(binding.movieRecycler) {
            layoutManager = GridLayoutManager(context, recyclerColumns)
            adapter = movieAdapter
        }
    }

    private fun updateRecyclerColumnsBasedOffOrientation() {
        val windowWidth = resources.displayMetrics.run { widthPixels / density }
        val idealViewHolderWidth = resources.getDpAsFloat(R.dimen.target_discovery_viewholder_width)
        recyclerColumns = (windowWidth / idealViewHolderWidth).toInt()
        if (recyclerColumns == 0) recyclerColumns = 1
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                movieDiscoveryViewModel.state.collectLatest { state ->
                    onStateChanged(state)
                }
            }
        }
    }

    private fun onStateChanged(state: MovieDiscoveryState) {
        when (state) {
            MovieDiscoveryState.Loading -> onLoadingState()
            MovieDiscoveryState.Failure -> onFailureState()
            is MovieDiscoveryState.Success -> onSuccessState(state)
        }
    }

    private fun onLoadingState() {
        showShimmerIfRecyclerAndShimmerNotVisible()
    }

    private fun showShimmerIfRecyclerAndShimmerNotVisible() {
        if (binding.movieRecycler.visibility == View.GONE && binding.shimmerGroup.visibility == View.GONE) {
            binding.movieRecycler.visibility = View.GONE
            binding.shimmerGroup.visibility = View.VISIBLE
        }
    }

    private fun onFailureState() {
        binding.shimmerGroup.visibility = View.GONE
        Toast.makeText(
            context,
            getString(R.string.movie_discovery_fetch_failed_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onSuccessState(state: MovieDiscoveryState.Success) {
        movieAdapter.addMovies(state.movies)
        binding.movieRecycler.visibility = View.VISIBLE
        binding.shimmerGroup.visibility = View.GONE
    }

    private fun fetchMovies() {
        movieDiscoveryViewModel.fetchMoviePage()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBottomReached() {
        movieDiscoveryViewModel.fetchMoviePage()
    }
}