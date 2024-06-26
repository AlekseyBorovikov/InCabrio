package com.digitalsln.stanserhorn.ui.main

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.data.WifiConnectionState
import com.digitalsln.stanserhorn.databinding.ActivityMainBinding
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.ui.trip_log.TripLogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    companion object {
        const val EXTRA_WIFI_CONNECTION_LOST = "ag.bictech.sthb.incabrio.WIFI_CONNECTION_LOST"
        private const val MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10000
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    private var wifiErrorDialog: Dialog? = null
    private var fineLocationAccessDialog: Dialog? = null
    private val connectionSnackBar: Snackbar by lazy {
        Snackbar.make(this, binding.navHostFragment, "Falsches WLAN verbunden. Bitte mit ${viewModel.getCorrectWifiName()} verbinden", Snackbar.LENGTH_INDEFINITE).apply {
            // Set Margins
            val params = view.layoutParams as FrameLayout.LayoutParams
            val left = binding.navigationRail.width + params.leftMargin
            params.setMargins(left, params.topMargin, params.rightMargin, params.bottomMargin)
            view.setLayoutParams(params)
            // Set Colors
            setBackgroundTint(this@MainActivity.getColor(R.color.attention_red))
        }
    }

    private var isFineLocationRequestWasShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.navigationRail.setNavController(navHostFragment.navController)

        Logger.i("In MainActivity.onCreate: Main activity started.")

        if (intent.hasExtra(EXTRA_WIFI_CONNECTION_LOST)) {
            wifiConnectionLostAction()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (fineLocationAccessDialog?.isShowing == true && isFineLocationRequestWasShown) return

                    fineLocationAccessDialog = AlertDialog.Builder(this)
                        .setMessage("The application, unfortunately, cannot work correctly without this permission.")
                        .setTitle("Permission denied dialog")
                        .setCancelable(false)
                        .setPositiveButton("Open settings") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("Exit") { _, _ -> finishAffinity() }
                        .create()
                    fineLocationAccessDialog?.show()
                }
                isFineLocationRequestWasShown = true
                return
            }
            else -> Unit
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.startDataUpdateManager()

        // ask permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            } else fineLocationAccessDialog?.dismiss()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                showNetworkConnectionErrorDialog()
            } else wifiErrorDialog?.dismiss()
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000003)
            }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                val marker = state.wifiState
                when(marker) {
                    WifiConnectionState.DataIsLoading -> {
                        binding.navigationRail.setStateToConnectedSyncing()
                        if (connectionSnackBar.isShown) connectionSnackBar.dismiss()
                    }
                    WifiConnectionState.LastSyncOutdated -> {
                        binding.navigationRail.setStateToSyncOutdated()
                    }
                    WifiConnectionState.NoConnection -> {
                        binding.navigationRail.setStateToNotConnected()
                        wifiConnectionLostAction()
                    }
                    WifiConnectionState.ShowConnectionError -> showNetworkConnectionErrorDialog()
                    WifiConnectionState.WifiWasConnected,
                    WifiConnectionState.DataWasLoaded -> binding.navigationRail.setStateToConnected()
                    WifiConnectionState.WrongConnection -> { if (!connectionSnackBar.isShown) connectionSnackBar.show() }
                    else -> binding.navigationRail.setStateToNotConnected()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopDataUpdateManager()
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiErrorDialog?.dismiss()
        fineLocationAccessDialog?.dismiss()
        wifiErrorDialog = null
        fineLocationAccessDialog = null
    }

//    preferenceHelper.debug

    private fun wifiConnectionLostAction() {
        Logger.i("In MainActivity.wifiConnectionLostAction: Received intent, opening AddTripLogDialogFragment.")

        if(viewModel.canNavigateWhenWifiLost()) {
            val notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(this, notificationSoundUri)
            ringtone.play()

            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.guests, bundleOf(Pair(TripLogFragment.OPEN_DIALOG_KEY, true)))
        }
    }

    private fun showNetworkConnectionErrorDialog() {
        if (wifiErrorDialog?.isShowing == true) return

        wifiErrorDialog = AlertDialog.Builder(this)
            .setTitle("Network connection error")
            .setMessage("You have opted out of the ability to manage your Wi-Fi network.\nPlease go to settings and allow access.")
            .setNegativeButton("Close") {  dialog, _ -> }
            .create()
        wifiErrorDialog?.show()
    }
}