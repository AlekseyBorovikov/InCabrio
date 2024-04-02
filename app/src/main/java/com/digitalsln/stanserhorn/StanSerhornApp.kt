package com.digitalsln.stanserhorn

import android.app.Application
import com.digitalsln.stanserhorn.tools.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StanSerhornApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.initialize(this)
    }

}