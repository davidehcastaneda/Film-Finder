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
import androidx.navigation.fragment.findNavController
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.FragmentMovieDiscoveryBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDiscoveryFragment : DaggerFragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMovieDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSampleNavigation()
        observeViewModel()
        fetchMovies()
    }

    private fun setSampleNavigation() {
        binding.root.setOnClickListener {
            navigateToMovieDetails()
        }
    }

    private fun navigateToMovieDetails() {
        findNavController().navigate(R.id.action_movieDiscoveryFragment_to_movieDetailsFragment)
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
            MovieDiscoveryState.Failure -> onFailureState()
            is MovieDiscoveryState.Success -> onSuccessState(state)
        }
    }

    private fun onFailureState() {
        Toast.makeText(
            context,
            getString(R.string.movie_discovery_fetch_failed_message),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun onSuccessState(state: MovieDiscoveryState.Success) {
        Toast.makeText(context,
            getString(R.string.movie_discovery_fetch_succeeded_message),
            Toast.LENGTH_SHORT).show()
    }

    private fun fetchMovies() {
        movieDiscoveryViewModel.fetchMoviePage()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}