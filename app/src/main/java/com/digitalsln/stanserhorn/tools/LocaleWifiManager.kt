package com.digitalsln.stanserhorn.tools

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.WifiConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocaleWifiManager @Inject constructor(
    private val context: Context,
    private val preference: PreferenceHelper,
    private val wifiStateChannel: WifiStateChannel,
) {

    companion object {
        private const val TAG = "LocaleWifiManager"
        private const val MIN_WIFI_PASSWORD_CORRECT_LENGTH = 8
    }





    private val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager?

    /**
     * This value will only be called for new phones from s/31/And -12 -- Snow Cone
     * required ...  ACCESS_NETWORK_STATE and ACCESS_FINE_LOCATION permissions
     * need to pass FLAG_INCLUDE_LOCATION_INFO to NetworkCallback(), otherwise you will get "unknow ssid" only
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    private val getNetworkInfoCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
//                if (!startConnectionToCorrectWifiIfNeed(networkCapabilities.transportInfo as WifiInfo?)) actionOnCorrectWifi?.invoke()
                Logger.d("$TAG: wifi info was got ${networkCapabilities.transportInfo as WifiInfo}")
                actionOnCorrectWifi?.invoke()
            }
        }
    } else null

    /**
     * Access connected wifi info from this var ..because we are using listener .. to set value ..
     * @see setCallBack method ..
     */
    private val connectivityManager = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
        registerNetworkCallback(
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Logger.d("$TAG: available network $network")
                    CoroutineScope(Dispatchers.IO).launch {
                        wifiStateChannel.send(WifiConnectionState.WifiWasConnected)
                    }
                }

                override fun onLost(network: Network) {
                    Logger.d("$TAG: lost network $network")
                    try {
                        if (getNetworkInfoCallback != null) unregisterNetworkCallback(getNetworkInfoCallback)
                    } catch (e: Exception) {}

                    CoroutineScope(Dispatchers.IO).launch {
                        wifiStateChannel.send(WifiConnectionState.NoConnection)
                    }
                }
            }
        )
    }

    private var actionOnCorrectWifi: (() -> Unit)? = null





    @Synchronized
    fun doOnCorrectWifiNetwork(onSuccess: () -> Unit, onError: () -> Unit) {
        actionOnCorrectWifi = onSuccess

        if (!checkFineLocation()) return Logger.w("$TAG: No 'FineLocation' permission")

        // If Wi-Fi settings are invalid, return false
        if (isWifiSettingsInvalid()) return Logger.w("$TAG: Wifi settings are incorrect")
        if (wifiManager?.isWifiEnabled == true) {
            val network = connectivityManager.activeNetwork

            if (network == null) {
                setupDeviceNetwork()
                return
            }
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return onError()

            // If your device is connected to Wi-Fi
            if (!networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Logger.i("$TAG: device is not connected to Wi-Fi")
                return onError()
            }

            checkCurrentConnectionAndExecuteOrConnect()
        } else onError()
    }

    // Checks if Wi-Fi settings are valid
    fun isWifiSettingsInvalid(): Boolean {
        // If the SSID is empty or the password is shorter than 8 characters, the settings are considered invalid
        return preference.correctWifiSsid.isEmpty() || preference.wifiPassword.length < MIN_WIFI_PASSWORD_CORRECT_LENGTH
    }

    fun createRequestConnectToCorrectWifiNetwork() {
        // Check Wi-Fi settings
        if (isWifiSettingsInvalid()) { return }

        val ssid = preference.correctWifiSsid
        val password = preference.wifiPassword
        Logger.d("$TAG: try to connect to network (ssid: $ssid | password: $password)")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

            if (wifiManager == null) return Logger.e("$TAG: wifiManager is null")
            val currentSuggestions = wifiManager.networkSuggestions
            wifiManager.removeNetworkSuggestions(currentSuggestions)
            val status = wifiManager.addNetworkSuggestions(listOf(suggestion))
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Logger.d("$TAG: fail connection to network (ssid: $ssid | password: $password)")

                CoroutineScope(Dispatchers.IO).launch {
                    wifiStateChannel.send(WifiConnectionState.ShowConnectionError)
                }
            } else {
                Logger.d("$TAG: reset WiFi Manager")
                requestScanNetwork()
            }
        }
    }



    @SuppressLint("MissingPermission")
    private fun isSsidKnown(ssid: String): Boolean {
        var isInList = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val networks = if (checkFineLocation()) {
                wifiManager?.scanResults ?: listOf()
            } else {
                Logger.w("$TAG: No 'FineLocation' permission")
                listOf()
            }

            for(network in networks) {
                val networkSsid = network.wifiSsid
                if (networkSsid.toString().replace("\"", "") == ssid) {
                    Logger.d("$TAG: Found wifi configuration to network with SSID $networkSsid.")
                    isInList = true
                    break
                }
            }
        } else {
            val wifiList = wifiManager?.configuredNetworks ?: listOf()
            for (i in wifiList.indices) {
                val conf = wifiList[i]
                val networkSsid = conf.SSID
                if (networkSsid == ssid) {
                    Logger.d("In LocaleWifiManager: Found wifi configuration to network with SSID $networkSsid.")
                    isInList = true
                    break
                }
            }
        }

        return isInList
    }

    private fun getSsidFromInfo(connectedWifiInfo: WifiInfo) = "\"${connectedWifiInfo.ssid.replace("^\"(.*)\"$".toRegex(), "$1")}\""

    private fun isCorrectSsid(ssid: String) = ssid.replace("\"", "") == preference.correctWifiSsid

    /**
     * This method will set the connected wifi name for all phones
     * @see connectedWifiName variable
     * must be called when permission is granted ..
     * @see [https://developer.android.com/reference/kotlin/android/net/wifi/WifiManager#getConnectionInfo()]
     */
    private fun checkCurrentConnectionAndExecuteOrConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            Logger.i("$TAG: create a request for information about Wi-Fi (api version 31 and higher)")
            connectivityManager.registerNetworkCallback(request, getNetworkInfoCallback ?: return)
        } else {
            val wifiInfo = wifiManager?.connectionInfo
            if (wifiInfo?.supplicantState == SupplicantState.COMPLETED) {
//                if(!startConnectionToCorrectWifiIfNeed(wifiInfo)) actionOnCorrectWifi?.invoke()
                val actualSSID = getSsidFromInfo(wifiInfo)

                // If the SSID of the current network does not match the SSID of the network to which the device should be connected, return false
                if (!isCorrectSsid(actualSSID)) {
                    Logger.w("$TAG: the current ssid does not match what is specified in the settings")
                    setupDeviceNetwork()

                    return
                }

                actionOnCorrectWifi?.invoke()
            }
        }
    }

    private fun setupDeviceNetwork() {
        CoroutineScope(Dispatchers.IO).launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (!isSsidKnown(preference.correctWifiSsid)) {
                    Logger.w("$TAG: current ssid is unknown")
                    connectCorrectWifi()
                }
                removeAllWifiNetworks()
            }
            else {
                val networkSuggestion = wifiManager?.networkSuggestions?.firstOrNull()
                if (networkSuggestion?.ssid != preference.correctWifiSsid || networkSuggestion.passphrase != preference.wifiPassword) {
                    createRequestConnectToCorrectWifiNetwork()
//                    wifiStateChannel.send(WifiConnectionState.ShowNeedConnectMessage)
                }
            }
        }
    }

    private fun connectCorrectWifi() {
        // Check Wi-Fi settings
        if (isWifiSettingsInvalid()) { return }

        val ssid = preference.correctWifiSsid
        val password = preference.wifiPassword

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {
                val wifiConfig = WifiConfiguration()
                wifiConfig.SSID = "\"" + ssid + "\""
                wifiConfig.preSharedKey = "\"" + password + "\""
                wifiConfig.hiddenSSID = !(preference.wifiVisible)
                val netId = wifiManager!!.addNetwork(wifiConfig)
                if (netId < 0) {
                    Logger.e("$TAG: ssid ($ssid) or password ($password) is incorrect.")
                } else {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(netId, true)
                    wifiManager.saveConfiguration()
                    if(wifiManager.reconnect()) actionOnCorrectWifi?.invoke()
                }
            } catch (e: Exception) {
                Logger.e("failed to add new network", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun removeAllWifiNetworks() {
        val wifiManager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val networks = wifiManager.configuredNetworks
        for (network in networks) {
            wifiManager.removeNetwork(network.networkId)
        }
        wifiManager.saveConfiguration()
    }

    private fun checkFineLocation(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestScanNetwork() {

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : WifiManager.ScanResultsCallback() {
            override fun onScanResultsAvailable() {
                Logger.d("$TAG: Network Scan Succeeds")
            }
        }
        wifiManager.registerScanResultsCallback(executor, callback)

    }

}