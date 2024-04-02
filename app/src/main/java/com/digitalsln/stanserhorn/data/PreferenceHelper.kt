package com.digitalsln.stanserhorn.data

import android.content.Context
import android.content.SharedPreferences
import com.digitalsln.stanserhorn.R

class PreferenceHelper(private val context: Context, dbName: String){

    object PreferenceVariable {
        const val KEY_RESERVATION_LATENCY = "preferences_reservationLatency"
        const val KEY_AUTOMATICALLY_OPEN_ADD_TRIP_LOG = "preferences_automatically_open_addTripLog"
        const val KEY_UNLOCK = "preferences_unlocked"
        const val KEY_CABIN_NUMBER = "preferences_cabin_number"
        const val KEY_CORRECT_WIFI_SSID = "preferences_correct_wifi_ssid"
        const val KEY_WIFI_PASSWORD = "preferences_wifi_password"
        const val KEY_WIFI_VISIBLE = "preferences_wifi_visible"
        const val KEY_LOCKED_ITEMS_CATEGORY = "preferences_locked_items_category"
        const val KEY_MINUTES_SINCE_LAST_SYNC_BEFORE_WARNING = "preferences_minutes_since_last_sync_before_warning"
        const val KEY_DEBUG = "preferences_debug_mode"
        const val KEY_FILL_DATABASE_WITH_DUMMY_VALUES = "preferences_fill_database_with_dummy_values"
        const val KEY_NETWORK_POLLING_INTERVAL = "preferences_network_polling_interval"
        const val KEY_LOG_LEVEL = "preferences_log_level"
        const val KEY_LOGFILE_AUTOMATIC_UPLOAD_THRESHOLD = "preferences_log_threshold_for_auto_upload"
        const val KEY_N_SYNC_LOG_ENTRIES_TO_KEEP = "preferences_n_sync_log_entries_to_keep"
        const val KEY_N_SYNC_LOG_CALLS_BEFORE_CLEANING = "preferences_n_sync_log_calls_before_cleaning"
        const val KEY_NETWORK_CONNECT_TIMEOUT = "preferences_network_connect_timeout"
        const val KEY_NETWORK_READ_TIMEOUT = "preferences_network_read_timeout"

        const val KEY_INFOBOARD_URL = "preferences_infoboard_url"
        const val KEY_DAILYMENU_URL = "preferences_dailymenu_url"
        const val KEY_RESERVATIONS_URL = "preferences_reservations_url"
        const val KEY_TRIPLOG_DOWNLOAD_URL = "preferences_triplog_download_url"
        const val KEY_TRIPLOG_UPLOAD_URL = "preferences_triplog_upload_url"
        const val KEY_LOGFILE_LISTENER_URL = "preferences_logfile_listener_url"
        const val KEY_LOGFILE_UPLOAD_URL = "preferences_logfile_upload_url"
    }

    private val appPrefs: SharedPreferences = context.getSharedPreferences(dbName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = appPrefs.edit()

    init { editor.apply() }

    var reservationLatency: String
        get() = appPrefs.getString(PreferenceVariable.KEY_RESERVATION_LATENCY, "") ?: context.getString(
            R.string.preferences_reservationLatency_defaultValue
        )
        set(reservationLatencyValue) {
            editor.putString(PreferenceVariable.KEY_RESERVATION_LATENCY, reservationLatencyValue)
            editor.apply()
        }

    var lockedItemsCategory: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_LOCKED_ITEMS_CATEGORY, true)
        set(lockedItemsCategoryValue) {
            editor.putBoolean(PreferenceVariable.KEY_LOCKED_ITEMS_CATEGORY, lockedItemsCategoryValue)
            editor.apply()
        }

    var automaticallyOpenAddTripLog: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_AUTOMATICALLY_OPEN_ADD_TRIP_LOG, false)
        set(automaticallyOpenAddTripLogValue) {
            editor.putBoolean(PreferenceVariable.KEY_AUTOMATICALLY_OPEN_ADD_TRIP_LOG, automaticallyOpenAddTripLogValue)
            editor.apply()
        }

    var unlock: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_UNLOCK, false)
        set(unlockValue) {
            editor.putBoolean(PreferenceVariable.KEY_UNLOCK, unlockValue)
            editor.apply()
        }

    var cabinNumber: String
        get() = appPrefs.getString(PreferenceVariable.KEY_CABIN_NUMBER, "0") ?: context.getString(R.string.preferences_cabin_number_defaultValue)
        set(cabinNumberValue) {
            editor.putString(PreferenceVariable.KEY_CABIN_NUMBER, cabinNumberValue)
            editor.apply()
        }

    var correctWifiSsid: String
        get() = appPrefs.getString(PreferenceVariable.KEY_CORRECT_WIFI_SSID, "") ?: context.getString(R.string.preferences_correct_wifi_ssid_defaultValue)
        set(correctWifiSsidValue) {
            editor.putString(PreferenceVariable.KEY_CORRECT_WIFI_SSID, correctWifiSsidValue)
            editor.apply()
        }

    var wifiPassword: String
        get() = appPrefs.getString(PreferenceVariable.KEY_WIFI_PASSWORD, "") ?: context.getString(R.string.preferences_wifi_password_defaultValue)
        set(wifiPasswordValue) {
            editor.putString(PreferenceVariable.KEY_WIFI_PASSWORD, wifiPasswordValue)
            editor.apply()
        }

    var wifiVisible: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_WIFI_VISIBLE, true)
        set(wifiVisibleValue) {
            editor.putBoolean(PreferenceVariable.KEY_WIFI_VISIBLE, wifiVisibleValue)
            editor.apply()
        }

    var minutesSinceLastSyncBeforeWarning: String
        get() = appPrefs.getString(PreferenceVariable.KEY_MINUTES_SINCE_LAST_SYNC_BEFORE_WARNING, "") ?: context.getString(R.string.preferences_minutes_since_last_sync_before_warning_defaultValue)
        set(minutesSinceLastSyncBeforeWarningValue) {
            editor.putString(PreferenceVariable.KEY_MINUTES_SINCE_LAST_SYNC_BEFORE_WARNING, minutesSinceLastSyncBeforeWarningValue)
            editor.apply()
        }

    var debug: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_DEBUG, false)
        set(debugValue) {
            editor.putBoolean(PreferenceVariable.KEY_DEBUG, debugValue)
            editor.apply()
        }

    var fillDatabaseWithDummyValues: Boolean
        get() = appPrefs.getBoolean(PreferenceVariable.KEY_FILL_DATABASE_WITH_DUMMY_VALUES, false)
        set(fillDatabaseWithDummyValuesValue) {
            editor.putBoolean(PreferenceVariable.KEY_FILL_DATABASE_WITH_DUMMY_VALUES, fillDatabaseWithDummyValuesValue)
            editor.apply()
        }

    var networkPollingInterval: String
        get() = appPrefs.getString(PreferenceVariable.KEY_NETWORK_POLLING_INTERVAL, context.getString(R.string.preferences_network_polling_interval_defaultValue)) ?: context.getString(R.string.preferences_network_polling_interval_defaultValue)
        set(networkPollingIntervalValue) {
            editor.putString(PreferenceVariable.KEY_NETWORK_POLLING_INTERVAL, networkPollingIntervalValue)
            editor.apply()
        }

    var logLevel: String
        get() = appPrefs.getString(PreferenceVariable.KEY_LOG_LEVEL, context.getString(R.string.preferences_log_level_defaultValue)) ?: context.getString(R.string.preferences_log_level_defaultValue)
        set(logLevelValue) {
            editor.putString(PreferenceVariable.KEY_LOG_LEVEL, logLevelValue)
            editor.apply()
        }

    var logfileAutomaticUploadThreshold: String
        get() = appPrefs.getString(PreferenceVariable.KEY_LOGFILE_AUTOMATIC_UPLOAD_THRESHOLD, context.getString(R.string.preferences_log_threshold_for_auto_upload_defaultValue)) ?: context.getString(R.string.preferences_log_threshold_for_auto_upload_defaultValue)
        set(logfileAutomaticUploadThresholdValue) {
            editor.putString(PreferenceVariable.KEY_LOGFILE_AUTOMATIC_UPLOAD_THRESHOLD, logfileAutomaticUploadThresholdValue)
            editor.apply()
        }

    var nSyncLogEntriesToKeep: String
        get() = appPrefs.getString(PreferenceVariable.KEY_N_SYNC_LOG_ENTRIES_TO_KEEP, context.getString(R.string.preferences_n_sync_log_entries_to_keep_defaultValue)) ?: context.getString(R.string.preferences_n_sync_log_entries_to_keep_defaultValue)
        set(nSyncLogEntriesToKeepValue) {
            editor.putString(PreferenceVariable.KEY_N_SYNC_LOG_ENTRIES_TO_KEEP, nSyncLogEntriesToKeepValue)
            editor.apply()
        }

    var nSyncLogCallsBeforeCleaning: String
        get() = appPrefs.getString(PreferenceVariable.KEY_N_SYNC_LOG_CALLS_BEFORE_CLEANING, context.getString(R.string.preferences_n_sync_log_calls_before_cleaning_defaultValue)) ?: context.getString(R.string.preferences_n_sync_log_calls_before_cleaning_defaultValue)
        set(nSyncLogCallsBeforeCleaningValue) {
            editor.putString(PreferenceVariable.KEY_N_SYNC_LOG_CALLS_BEFORE_CLEANING, nSyncLogCallsBeforeCleaningValue)
            editor.apply()
        }

    var networkConnectTimeout: String
        get() = appPrefs.getString(PreferenceVariable.KEY_NETWORK_CONNECT_TIMEOUT, context.getString(R.string.preferences_network_connect_timeout_defaultValue)) ?: context.getString(R.string.preferences_network_connect_timeout_defaultValue)
        set(networkConnectTimeoutValue) {
            editor.putString(PreferenceVariable.KEY_NETWORK_CONNECT_TIMEOUT, networkConnectTimeoutValue)
            editor.apply()
        }

    var networkReadTimeout: String
        get() = appPrefs.getString(PreferenceVariable.KEY_NETWORK_READ_TIMEOUT, context.getString(R.string.preferences_network_connect_read_defaultValue)) ?: context.getString(R.string.preferences_network_connect_read_defaultValue)
        set(networkReadTimeoutValue) {
            editor.putString(PreferenceVariable.KEY_NETWORK_READ_TIMEOUT, networkReadTimeoutValue)
            editor.apply()
        }

    var infoboardUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_INFOBOARD_URL, context.getString(R.string.preferences_infoboard_url_defaultValue)) ?: context.getString(R.string.preferences_infoboard_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_INFOBOARD_URL, url)
            editor.apply()
        }
    var dailyMenuUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_DAILYMENU_URL, context.getString(R.string.preferences_dailymenu_url_defaultValue)) ?: context.getString(R.string.preferences_dailymenu_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_DAILYMENU_URL, url)
            editor.apply()
        }
    var reservationsUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_RESERVATIONS_URL, context.getString(R.string.preferences_reservations_url_defaultValue)) ?: context.getString(R.string.preferences_reservations_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_RESERVATIONS_URL, url)
            editor.apply()
        }
    var tripLogDownloadUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_TRIPLOG_DOWNLOAD_URL, context.getString(R.string.preferences_triplog_download_defaultValue)) ?: context.getString(R.string.preferences_triplog_download_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_TRIPLOG_DOWNLOAD_URL, url)
            editor.apply()
        }
    var tripLogUploadUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_TRIPLOG_UPLOAD_URL, context.getString(R.string.preferences_triplog_upload_url_defaultValue)) ?: context.getString(R.string.preferences_triplog_upload_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_TRIPLOG_UPLOAD_URL, url)
            editor.apply()
        }
    var logFileListenerUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_LOGFILE_LISTENER_URL, context.getString(R.string.preferences_logfile_listener_url_defaultValue)) ?: context.getString(R.string.preferences_logfile_listener_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_LOGFILE_LISTENER_URL, url)
            editor.apply()
        }
    var logFileUploadUrl: String
        get() = appPrefs.getString(PreferenceVariable.KEY_LOGFILE_UPLOAD_URL, context.getString(R.string.preferences_logfile_upload_url_defaultValue)) ?: context.getString(R.string.preferences_logfile_upload_url_defaultValue)
        set(url) {
            editor.putString(PreferenceVariable.KEY_LOGFILE_UPLOAD_URL, url)
            editor.apply()
        }
}