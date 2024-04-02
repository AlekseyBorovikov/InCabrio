package com.digitalsln.stanserhorn.tools

interface StateChannel<T> {
    suspend fun send(value: T)
    suspend fun observe(block: (T) -> Unit)
}