package com.digitalsln.stanserhorn.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<T: ViewState>: ViewModel() {

    private val _uiState = MutableStateFlow(initState())
    val uiState = _uiState.asStateFlow()

    abstract fun initState(): T

    fun updateState(function: T.() -> T) {
        _uiState.update { state -> function(state) }
    }

    private val handler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    open fun handleError(exception: Throwable) {}

    open fun startLoading() {}

    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(handler, block = block)
    }

    companion object {
        private const val SAFE_LAUNCH_EXCEPTION = "ViewModel-ExceptionHandler"
    }
}