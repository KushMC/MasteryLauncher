package com.redemastery.launcher.core.base


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<STATE, EVENT>(
    initState: STATE,
) : ViewModel() {

    protected val _state = MutableStateFlow(initState)
    val state = _state.asStateFlow()

    abstract fun onEvent(event: EVENT)

}