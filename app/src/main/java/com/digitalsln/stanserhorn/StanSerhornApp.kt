package com.digitalsln.stanserhorn

import android.app.Application
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.tools.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StanSerhornApp: Application() {

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate() {
        super.onCreate()

        Logger.initialize(this, preferenceHelper.logLevel, preferenceHelper.logfileAutomaticUploadThreshold)
    }

}