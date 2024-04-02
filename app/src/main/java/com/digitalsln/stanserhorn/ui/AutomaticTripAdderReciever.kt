package com.digitalsln.stanserhorn.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.WifiConnectionState
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.WifiStateChannel
import com.digitalsln.stanserhorn.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AutomaticTripAdderReciever: BroadcastReceiver() {

    @Inject
    lateinit var wifiStateChannel: WifiStateChannel

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.d("In AutomaticTripAdderReciever.onReceive: Broadcast received.")

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: return
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting) {
            if (preferenceHelper.automaticallyOpenAddTripLog) {
                Logger.d("In AutomaticTripAdderReciever.onReceive: Losing network connection and automatic trip adding is enabled: opening dialog.")
                doInBackground { wifiStateChannel.send(WifiConnectionState.NoConnection) }
                val inCabrioIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(MainActivity.EXTRA_WIFI_CONNECTION_LOST, true)
                }
                context?.startActivity(inCabrioIntent)
            }
        }
    }

    private fun doInBackground(block: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch { block.invoke() }
    }


}