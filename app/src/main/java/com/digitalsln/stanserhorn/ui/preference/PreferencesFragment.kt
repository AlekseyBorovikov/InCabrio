package com.digitalsln.stanserhorn.ui.preference

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.tools.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment: PreferenceFragmentCompat(), OnSharedPreferenceChangeListener, UnlockDialogFragment.UnlockDialogListener {

    @Inject
    lateinit var preferences: PreferenceHelper

    private val viewModel by viewModels<PreferenceViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "InCabrioPrefs"
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.d("In PreferencesFragment.onCreate: Preferences created.");

        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_RESERVATION_LATENCY)?.summary = "${resources.getString(R.string.preferences_reservationLatency_summary)} (${preferences.reservationLatency} h)"
        // Set the text of the cabin number preferenceGhb
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_CABIN_NUMBER)?.summary = "${resources.getString(R.string.preferences_cabin_number_summary)} (${preferences.cabinNumber})"
        // Set the text of the StHB SSID preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_CORRECT_WIFI_SSID)?.summary = preferences.correctWifiSsid
        // Set the text for the minutes since last sync before warning
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_MINUTES_SINCE_LAST_SYNC_BEFORE_WARNING)?.summary = "${resources.getString(R.string.preferences_minutes_since_last_sync_before_warning_summary)} (${preferences.minutesSinceLastSyncBeforeWarning} Minuten)"
        // Set the text of the polling interval preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_NETWORK_POLLING_INTERVAL)?.summary = "${resources.getString(R.string.preferences_network_polling_interval_summary)} (${preferences.networkPollingInterval} s)"
        // Set the text of the infoboard URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_INFOBOARD_URL)?.summary = preferences.infoboardUrl
        // Set the text of the dailymenu URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_DAILYMENU_URL)?.summary = preferences.dailyMenuUrl
        // Set the text of the reservations URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_RESERVATIONS_URL)?.summary = preferences.reservationsUrl
        // Set the text of the triplog download URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_DOWNLOAD_URL)?.summary = preferences.tripLogDownloadUrl
        // Set the text of the triplog upload URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_UPLOAD_URL)?.summary = preferences.tripLogUploadUrl
        // Set the text of the logfile listener URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_LOGFILE_LISTENER_URL)?.summary = preferences.logFileListenerUrl
        // Set the text of the upload logfile URL preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_LOGFILE_UPLOAD_URL)?.summary = preferences.logFileUploadUrl
        // Set the text for the n sync log entries to keep
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_N_SYNC_LOG_ENTRIES_TO_KEEP)?.summary = "${resources.getString(R.string.preferences_n_sync_log_entries_to_keep_summary)} (${preferences.nSyncLogEntriesToKeep})"
        // Set the text for the n sync log calls before cleaning
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_N_SYNC_LOG_CALLS_BEFORE_CLEANING)?.summary = "${resources.getString(R.string.preferences_n_sync_log_calls_before_cleaning_summary)} (${preferences.nSyncLogCallsBeforeCleaning})"
        // Set the text of the connect timeout preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_NETWORK_CONNECT_TIMEOUT)?.summary = "${resources.getString(R.string.preferences_network_connect_timeout_summary)} (${preferences.networkConnectTimeout} s)"
        // Set the text of the read timeout preference
        findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_NETWORK_READ_TIMEOUT)?.summary = "${resources.getString(R.string.preferences_network_connect_read_summary)} (${preferences.networkReadTimeout} s)"
    }

    private var mDialogActive = false

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key) {
            PreferenceHelper.PreferenceVariable.KEY_RESERVATION_LATENCY -> {
                // Set the text of the reservation latency preference
                // Note that this condition also appears further down to force an update of the show columns of all tables
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_RESERVATION_LATENCY)?.summary = resources.getString(R.string.preferences_reservationLatency_summary) + " (" + preferences.reservationLatency + " h)"
            }
            PreferenceHelper.PreferenceVariable.KEY_UNLOCK -> {
                // Invoke the preferences unlock password dialog
                if (mDialogActive) { return }
                val unlocked = preferences.unlock
                if (unlocked) {
                    showPasswordDialog()
                    findPreference<SwitchPreference>(PreferenceHelper.PreferenceVariable.KEY_UNLOCK)?.isChecked = false
                } else {
                    findPreference<Preference>(PreferenceHelper.PreferenceVariable.KEY_LOCKED_ITEMS_CATEGORY)?.isEnabled = false
                }
            }
            PreferenceHelper.PreferenceVariable.KEY_CABIN_NUMBER -> {
                // Set the text of the cabin number preference
                findPreference<EditTextPreference>(key)?.summary = "${resources.getString(R.string.preferences_cabin_number_summary)} (${preferences.cabinNumber})"
            }
            PreferenceHelper.PreferenceVariable.KEY_CORRECT_WIFI_SSID -> {
                // Set the text of the StHB SSID preference
                findPreference<EditTextPreference>(key)?.summary = preferences.correctWifiSsid
            }
            PreferenceHelper.PreferenceVariable.KEY_MINUTES_SINCE_LAST_SYNC_BEFORE_WARNING -> {
                // Set the text for the minutes since last sync before warning
                findPreference<EditTextPreference>(key)?.summary = "${resources.getString(R.string.preferences_minutes_since_last_sync_before_warning_summary)} (${preferences.minutesSinceLastSyncBeforeWarning} Minuten)"
            }
            PreferenceHelper.PreferenceVariable.KEY_NETWORK_POLLING_INTERVAL -> {
                // Set the summary to display the interval which is set
                findPreference<EditTextPreference>(key)?.summary = "${resources.getString(R.string.preferences_network_polling_interval_summary)} (${preferences.networkPollingInterval} s)"
                viewModel.updateLoadInterval()
            }
            PreferenceHelper.PreferenceVariable.KEY_INFOBOARD_URL -> {
                // Set the text of the infoboard URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_INFOBOARD_URL)?.summary = preferences.infoboardUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_DAILYMENU_URL -> {
                // Set the text of the dailymenu URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_DAILYMENU_URL)?.summary = preferences.dailyMenuUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_RESERVATIONS_URL -> {
                // Set the text of the reservations URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_RESERVATIONS_URL)?.summary = preferences.reservationsUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_DOWNLOAD_URL -> {
                // Set the text of the triplog download URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_DOWNLOAD_URL)?.summary = preferences.tripLogDownloadUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_UPLOAD_URL -> {
                // Set the text of the triplog upload URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_TRIPLOG_UPLOAD_URL)?.summary = preferences.tripLogUploadUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_LOGFILE_LISTENER_URL -> {
                // Set the text of the logfile listener URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_LOGFILE_LISTENER_URL)?.summary = preferences.logFileListenerUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_LOGFILE_UPLOAD_URL -> {
                // Set the text of the upload logfile URL preference
                findPreference<EditTextPreference>(PreferenceHelper.PreferenceVariable.KEY_LOGFILE_UPLOAD_URL)?.summary = preferences.logFileUploadUrl
            }
            PreferenceHelper.PreferenceVariable.KEY_LOG_LEVEL -> {
                // The next line is necessary so that the list preference is correctly displayed
                // even when the preference was edited from the NetworkCommunicator and not by
                // the user
                findPreference<ListPreference>(key)?.value = preferences.logLevel
                // Set the log levels, which is happening in getInstance()
                Logger.getInstance()?.setLogLevel(preferences.logLevel)
            }
            PreferenceHelper.PreferenceVariable.KEY_LOGFILE_AUTOMATIC_UPLOAD_THRESHOLD -> {
                // Set the log levels, which is happening in getInstance()
                Logger.getInstance()?.setLogLevelThreshold(preferences.logfileAutomaticUploadThreshold)
            }
            PreferenceHelper.PreferenceVariable.KEY_NETWORK_CONNECT_TIMEOUT -> {
                // Set the text of the connect timeout preference
                findPreference<EditTextPreference>(key)?.summary = "${preferences.networkConnectTimeout} s"
                viewModel.recreateClient()
            }
            PreferenceHelper.PreferenceVariable.KEY_N_SYNC_LOG_ENTRIES_TO_KEEP -> {
                // Set the text for the n sync log entries to keep
                findPreference<EditTextPreference>(key)?.summary = "${resources.getString(R.string.preferences_n_sync_log_entries_to_keep_summary)} (${preferences.nSyncLogEntriesToKeep})"
            }
            PreferenceHelper.PreferenceVariable.KEY_N_SYNC_LOG_CALLS_BEFORE_CLEANING -> {
                // Set the text for the n sync log calls before cleaning
                findPreference<EditTextPreference>(key)?.summary = "${resources.getString(R.string.preferences_n_sync_log_calls_before_cleaning_summary)} (${preferences.nSyncLogCallsBeforeCleaning})"
            }
            PreferenceHelper.PreferenceVariable.KEY_NETWORK_READ_TIMEOUT -> {
                // Set the text of the read timeout preference
                findPreference<EditTextPreference>(key)?.summary = "${preferences.networkReadTimeout} s"
                viewModel.recreateClient()
            }
            PreferenceHelper.PreferenceVariable.KEY_FILL_DATABASE_WITH_DUMMY_VALUES -> {
                // Fill the database with dummy values using the AsyncTask defined below
                if (preferences.fillDatabaseWithDummyValues) {
                    viewModel.fillDummyData()
                } else {
                    viewModel.removeDummyData()
                }
            }
        }
    }

    // This is for the preferences unlock password dialog
    override fun showPasswordDialog() {
        mDialogActive = true
        val unlockDialog = UnlockDialogFragment()
        unlockDialog.registerUnlockDialogListener(this)
        unlockDialog.show(activity?.supportFragmentManager ?: return, "unlockDialog")
    }

    // This is for the preferences unlock password dialog
    override fun onUnlockDialogSuccessful() {
        val lockedSwitchPreference = findPreference<SwitchPreference>(PreferenceHelper.PreferenceVariable.KEY_UNLOCK)
        lockedSwitchPreference?.setChecked(true)
        val lockedItemsCategory = findPreference<Preference>(PreferenceHelper.PreferenceVariable.KEY_LOCKED_ITEMS_CATEGORY)
        lockedItemsCategory?.isEnabled = true
        mDialogActive = false
    }

    // This is for the preferences unlock password dialog
    override fun onUnlockDialogUnsuccessful() {
        val lockedSwitchPreference = findPreference<SwitchPreference>(PreferenceHelper.PreferenceVariable.KEY_UNLOCK)
        lockedSwitchPreference?.setChecked(false)
        val lockedItemsCategory = findPreference<Preference>(PreferenceHelper.PreferenceVariable.KEY_LOCKED_ITEMS_CATEGORY)
        lockedItemsCategory?.isEnabled = false
        mDialogActive = false
    }

    // This is for the preferences unlock password dialog
    override fun onUnlockDialogWrongPassword() {
        val wrongPasswordDialog = UnlockDialogFragment.WrongPasswordDialogFragment()
        wrongPasswordDialog.registerUnlockDialogListener(this)
        wrongPasswordDialog.show(activity?.supportFragmentManager ?: return, "wrongPasswordDialog")
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Lock the preferences when the activity gets paused
        findPreference<SwitchPreference>(PreferenceHelper.PreferenceVariable.KEY_UNLOCK)?.isChecked = false
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

}