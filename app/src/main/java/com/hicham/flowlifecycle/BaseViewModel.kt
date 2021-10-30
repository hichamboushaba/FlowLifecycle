package com.hicham.flowlifecycle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

open class BaseViewModel(application: Application): AndroidViewModel(application) {
    private val lifeCycleState = MutableSharedFlow<Lifecycle.State>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val lifecycleObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            lifeCycleState.tryEmit(source.lifecycle.currentState)
            if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                source.lifecycle.removeObserver(this)
            }
        }
    }

    fun startObservingLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    protected fun <T> Flow<T>.whenAtLeast(requiredState: Lifecycle.State): Flow<T> {
        return lifeCycleState.map { state -> state.isAtLeast(requiredState) }
            .distinctUntilChanged()
            .flatMapLatest {
                if (it) this else emptyFlow()
            }
    }
}