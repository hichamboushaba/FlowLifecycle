package com.hicham.flowlifecycle

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationObserver = LocationObserver(application)

    private val hasLocationPermission = MutableStateFlow(false)

    @SuppressLint("MissingPermission")
    val locationUpdates: Flow<Location> = hasLocationPermission
        .filter { it }
        .flatMapLatest { locationObserver.observeLocationUpdates() }

    fun onLocationPermissionGranted() {
        hasLocationPermission.value = true
    }
}

data class ViewState(
    val location: Location,
    val nearbyLocations: List<String>
)