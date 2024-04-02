package com.digitalsln.stanserhorn.tools

import com.digitalsln.stanserhorn.data.DataUpdateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class DataUpdateChannel @Inject constructor(): StateChannel<DataUpdateEvent> {

    private val channel = Channel<DataUpdateEvent>()
    private val sharedFlow = channel.consumeAsFlow().shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly)

    override suspend fun send(value: DataUpdateEvent) { channel.send(value) }

    override suspend fun observe(block: (DataUpdateEvent) -> Unit) { sharedFlow.collect { block.invoke(it) } }

}