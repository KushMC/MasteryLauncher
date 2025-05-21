package com.redemastery.launcher.presentation.compose.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun LifeCycleEffect(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {},
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val currentOnCreate = rememberUpdatedState(onCreate)
    val currentOnStart = rememberUpdatedState(onStart)
    val currentOnResume = rememberUpdatedState(onResume)
    val currentOnPause = rememberUpdatedState(onPause)
    val currentOnStop = rememberUpdatedState(onStop)
    val currentOnDestroy = rememberUpdatedState(onDestroy)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> currentOnCreate.value()
                Lifecycle.Event.ON_START -> currentOnStart.value()
                Lifecycle.Event.ON_RESUME -> currentOnResume.value()
                Lifecycle.Event.ON_PAUSE -> currentOnPause.value()
                Lifecycle.Event.ON_STOP -> currentOnStop.value()
                Lifecycle.Event.ON_DESTROY -> currentOnDestroy.value()
                Lifecycle.Event.ON_ANY -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}