package com.digitalsln.stanserhorn.tools

import java.util.concurrent.atomic.AtomicInteger

class DebugManager {

    private val synchronizationCounter = AtomicInteger(0)

    fun incrementSynchronizationCount() = synchronizationCounter.incrementAndGet()

}