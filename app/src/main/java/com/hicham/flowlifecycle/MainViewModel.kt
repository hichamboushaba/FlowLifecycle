package com.hicham.flowlifecycle

import android.app.Application
import android.location.Location
import androidx.lifecycle.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val locationObserver = LocationObserver(application)
    private val api: FakeApi = FakeApi()

    private val hasLocationPermission = MutableStateFlow(false)

    private val locationUpdates: Flow<Location> = hasLocationPermission
        .filter { it }
        .flatMapLatest { locationObserver.observeLocationUpdates() }
        .whenAtLeast(Lifecycle.State.STARTED)

    val viewState: Flow<ViewState> = locationUpdates
        .mapLatest { location ->
            val nearbyLocations = api.fetchNearbyLocations(location.latitude, location.longitude)
            ViewState(
                isLoading = false,
                location = location,
                nearbyLocations = nearbyLocations
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = ViewState(isLoading = true)
        )


    fun onLocationPermissionGranted() {
        hasLocationPermission.value = true
    }
}

data class ViewState(
    val isLoading: Boolean,
    val location: Location? = null,
    val nearbyLocations: List<String> = emptyList()
)