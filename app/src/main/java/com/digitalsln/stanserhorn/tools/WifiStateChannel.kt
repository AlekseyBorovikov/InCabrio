package com.digitalsln.stanserhorn.tools

import com.digitalsln.stanserhorn.data.WifiConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class WifiStateChannel @Inject constructor(): StateChannel<WifiConnectionState> {

    private val channel = Channel<WifiConnectionState>()
    private val sharedFlow = channel.consumeAsFlow().shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly)

    override suspend fun send(value: WifiConnectionState) {
        channel.send(value)
    }

    override suspend fun observe(block: (WifiConnectionState) -> Unit) {
        sharedFlow.collect { block.invoke(it) }
    }

}