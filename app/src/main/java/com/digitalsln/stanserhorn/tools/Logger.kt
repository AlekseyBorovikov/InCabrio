package com.digitalsln.stanserhorn.tools

import android.content.Context
import android.util.Log
import com.digitalsln.stanserhorn.R
import com.digitalsln.stanserhorn.tools.ext.writeToFile
import kotlinx.coroutines.sync.Semaphore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Logger private constructor(private val context: Context) {

    enum class LogLevel(val code: Int) {
        OFF(-1),
        DEBUG(0),
        INFO(1),
        WARNING(2),
        ERROR(3),
        FATAL(4),
    }

    private val s = Semaphore(1)

    private var sLogLevel = LogLevel.DEBUG

    private var sLogLevelThresholdForUpload = LogLevel.WARNING
    var needUploadToServer = false
        get() {
            val getNeedUpload = field
            field = false
            return getNeedUpload
        }
        private set

    companion object {
        private const val LOGFILE_NAME = "inCabrioLog"

        private var instance: Logger? = null

        fun getFileName(): String {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            return "${LOGFILE_NAME}_${sdf.format(Date())}.txt"
        }

        fun initialize(context: Context, logLevel: String, logLevelThreshold: String) {
            instance = Logger(context).apply {
                setLogLevel(logLevel)
                setLogLevelThreshold(logLevelThreshold)
            }
        }

        fun getInstance(): Logger? {
            return instance
        }

        fun d(message: String) = getInstance()?.log(LogLevel.DEBUG, message) ?: Unit

        fun i(message: String) = getInstance()?.log(LogLevel.INFO, message) ?: Unit

        fun w(message: String) = getInstance()?.log(LogLevel.WARNING, message) ?: Unit

        fun e(message: String) = getInstance()?.log(LogLevel.ERROR, message) ?: Unit

        fun e(message: String, e: Throwable) = getInstance()?.log(LogLevel.ERROR, message, e) ?: Unit

        fun f(message: String) = getInstance()?.log(LogLevel.FATAL, message) ?: Unit

        fun f(message: String, e: Throwable) = getInstance()?.log(LogLevel.FATAL, message, e) ?: Unit
    }

    private fun log(level: LogLevel, message: String) {
        logToConsole(level, message)

        if (level.code < sLogLevel.code) return
        logToFile(level, message)
    }

    private fun log(level: LogLevel, message: String, e: Throwable) {
        logToConsole(level, message, e)

        if (level.code < sLogLevel.code) return
        logToFile(level, message, e)

        if(level >= sLogLevelThresholdForUpload) {
            log(LogLevel.INFO, "In Logger.log(): Automatic logfile upload requested.")
            needUploadToServer = true
        }
    }

    private fun logToFile(level: LogLevel, message: String) {
        if (s.tryAcquire()) {
            val safeLevel = if (level == LogLevel.OFF) {
                logToFile(LogLevel.FATAL, "Log level $level invalid.");
                LogLevel.FATAL
            } else level

            val logMessage = formatLogMessage(safeLevel, message)

            context.writeToFile(logMessage)
            s.release()
        }
    }

    private fun logToFile(level: LogLevel, message: String, e: Throwable) {
        val stackTraces = e.stackTrace
        var messageNew = message + "\n"
        for (i in stackTraces.indices) {
            messageNew += "at " + stackTraces[i].className +
                    stackTraces[i].methodName + "(" +
                    stackTraces[i].fileName + ":" +
                    stackTraces[i].lineNumber + ")\n"
        }
        logToFile(level, message)
    }

    private fun logToConsole(level: LogLevel, message: String, e: Throwable? = null) {
        when (level) {
            LogLevel.DEBUG -> Log.d("Logger", message, e)
            LogLevel.INFO -> Log.i("Logger", message, e)
            LogLevel.WARNING -> Log.w("Logger", message, e)
            LogLevel.ERROR, LogLevel.FATAL -> Log.e("Logger", message, e)
            else -> {
                logToConsole(LogLevel.FATAL, "Log level $level invalid.")
                logToConsole(LogLevel.FATAL, message, e)
            }
        }
    }

    private fun formatLogMessage(level: LogLevel, message: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return "[$timestamp] [$level]: $message"
    }

    fun setLogLevel(logLevel: String) {
        val correctLogLevel = convertStringToLoglevel(logLevel)
        if(sLogLevel != correctLogLevel) {
            val oldLogLevel = convertLoglevelToString(sLogLevel)
            sLogLevel = LogLevel.INFO
            log(LogLevel.INFO, "Log level changed from '$oldLogLevel' to '$logLevel'.");
            sLogLevel = correctLogLevel
        }
    }

    fun setLogLevelThreshold(logLevelThreshold: String) {
        val correctLogLevel = convertStringToLoglevel(logLevelThreshold)
        if(sLogLevel.code > correctLogLevel.code) {
            log(LogLevel.INFO, "In Logger.setLogLevelThreshold(): Found treshold for automatic uploading " +
                    "('" + logLevelThreshold + "'). Setting log level to " + logLevelThreshold + ".");
            setLogLevel(logLevelThreshold)
        }

        if (sLogLevelThresholdForUpload != correctLogLevel) {
            log(LogLevel.INFO, "Log level threshold for file upload changed from '$sLogLevelThresholdForUpload' to '$logLevelThreshold'.")
            sLogLevelThresholdForUpload = correctLogLevel
        }
    }

    private fun convertStringToLoglevel(upperLevelString: String): LogLevel {
        return LogLevel.valueOf(upperLevelString)
    }

    fun convertLoglevelToString(logLevel: LogLevel): String {
        return when (logLevel) {
            LogLevel.OFF -> context.resources.getStringArray(R.array.preferences_loglevel_spinner_choices)[0].toUpperCase()
            LogLevel.DEBUG -> "DEBUG"
            LogLevel.INFO -> "INFO"
            LogLevel.WARNING -> "WARNING"
            LogLevel.ERROR -> "ERROR"
            LogLevel.FATAL -> "FATAL"
            else -> {
                log(LogLevel.ERROR, "In Logger.convertLoglevelToString(): The Integer '$logLevel' is not a valid log level. Setting log level to INFO")
                "INFO"
            }
        }
    }
}