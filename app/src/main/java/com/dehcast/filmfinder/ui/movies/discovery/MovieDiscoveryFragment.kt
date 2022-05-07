package com.dehcast.filmfinder.ui.movies.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dehcast.filmfinder.R
import com.dehcast.filmfinder.databinding.FragmentMovieDiscoveryBinding

class MovieDiscoveryFragment : Fragment() {

    private lateinit var binding: FragmentMovieDiscoveryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMovieDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSampleNavigation()
    }

    private fun setSampleNavigation() {
        binding.root.setOnClickListener {
            navigateToMovieDetails()
        }
    }

    private fun navigateToMovieDetails() {
        findNavController().navigate(R.id.action_movieDiscoveryFragment_to_movieDetailsFragment)
    }

}