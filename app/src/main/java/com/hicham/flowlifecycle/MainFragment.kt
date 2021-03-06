package com.hicham.flowlifecycle

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hicham.flowlifecycle.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainFragment : BaseFragment<MainViewModel>(R.layout.fragment_main) {
    override val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.onLocationPermissionGranted()
            } else {
                // TODO
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMainBinding.bind(view)

        val hasLocationPermission =
            requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
        if (hasLocationPermission) {
            viewModel.onLocationPermissionGranted()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.viewState
                .onEach { viewState ->
                    binding.render(viewState)
                }
                .launchIn(this)
        }
    }

    private fun FragmentMainBinding.render(viewState: ViewState) {
        progressBar.isVisible = viewState.isLoading
        locationText.isVisible = !viewState.isLoading
        nearbyLocations.isVisible = !viewState.isLoading

        viewState.location?.let {
            locationText.text = "${it.latitude} ${it.longitude}"
        }
        nearbyLocations.text = viewState.nearbyLocations.joinToString(",")
    }
}


