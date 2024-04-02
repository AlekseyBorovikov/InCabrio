package com.digitalsln.stanserhorn.ui.components

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.repositoies.InternalLogRepository
import com.digitalsln.stanserhorn.tools.LocaleWifiManager
import com.digitalsln.stanserhorn.tools.Logger
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NetworkInfoDialog: DialogFragment() {
    private var isDialogShown = false
    @Inject
    lateinit var localeWifiManager: LocaleWifiManager
    @Inject
    lateinit var internalLogRepository: InternalLogRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        return builder
//            .setMessage(getDialogMessage())
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.network_state_dialog_ok)) { dialog, _ -> dismiss() }
            .create()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (isDialogShown) return
        isDialogShown = true
        super.show(manager, tag)
    }

    override fun dismiss() {
        isDialogShown = false
        super.dismiss()
    }

//    private fun getDialogMessage(): String {
//        var message = "Verbindungsstatus: "
//        with (resources) {
//            message += if (localeWifiManager.wifiSettingsInvalid()) {
//                getString(R.string.network_state_dialog_message_wifi_invalid)
//            } else {
//                when (InCabrio.sNetworkState) {
//                    InCabrio.CONNECTED -> getString(R.string.network_state_dialog_message_connected)
//                    InCabrio.SYNC_IN_PROGRESS -> getString(R.string.network_state_dialog_message_sync_in_progress)
//                    InCabrio.NOT_CONNECTED -> getString(R.string.network_state_dialog_message_not_connected)
//                    InCabrio.LAST_SYNC_OUTDATED -> getString(R.string.network_state_dialog_message_outdated)
//                    else -> ""
//                }
//            }
//        }
//        message += "\nLetzte erfolgreiche Synchronisation: "
//        val entry = internalLogRepository.getLatestSync()
//        message += if (entry == null) {
//            "-"
//        } else {
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
//            val syncDateTime = try {
//                dateFormat.parse("${entry.syncDate} ${entry.syncTime}")
//            } catch (e: ParseException) {
//                Logger.e("In SyncLogHelper.getLatestSync(): Could not parse " + "date string '${entry.syncDate} ${entry.syncTime}'.", e)
//                "ERROR"
//            }
//            syncDateTime?.let {
//                val outputDateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
//                outputDateFormat.format(it)
//            } ?: ""
//        }
//        message += "\nMinuten seit der letzten erfolgreichen Synchronisation: "
//        message += SyncLogHelper.minutesSinceLastSuccessfulSync(activity)
//        return message
//    }
}