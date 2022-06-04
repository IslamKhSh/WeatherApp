package com.musala.weatherApp.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.musala.weatherApp.core.BuildConfig
import com.musala.weatherApp.core.base.states.BaseAction
import com.musala.weatherApp.core.base.states.BaseViewState
import com.musala.weatherApp.core.base.states.StateTimeTravelDebugger
import com.musala.weatherApp.core.di.DefaultDispatcher
import com.musala.weatherApp.core.di.IoDispatcher
import com.musala.weatherApp.core.di.MainDispatcher
import com.musala.weatherApp.core.extensions.asLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.properties.Delegates

abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(initialState: ViewState) :
    ViewModel() {

    private var stateTimeTravelDebugger: StateTimeTravelDebugger? = null

    private val _viewState = MutableLiveData(initialState)
    val viewState = _viewState.asLiveData

    init {
        if (BuildConfig.DEBUG) {
            stateTimeTravelDebugger = StateTimeTravelDebugger(this::class.java.simpleName)
        }
    }

    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, old, new ->
        _viewState.value = new

        if (new != old) {
            stateTimeTravelDebugger?.apply {
                addStateTransition(old, new)
                logLast()
            }
        }
    }

    /**
     * use it to send action to change the state and log state transition in debug
     */
    fun sendAction(viewAction: ViewAction) {
        stateTimeTravelDebugger?.addAction(viewAction)
        state = actionMapper(viewAction)
    }

    /**
     * Map viewAction to viewState
     */
    protected abstract fun actionMapper(viewAction: ViewAction): ViewState
}
