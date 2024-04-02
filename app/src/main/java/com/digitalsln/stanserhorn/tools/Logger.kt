package com.digitalsln.stanserhorn.tools

import android.content.Context
import android.os.Environment
import android.util.Log
import com.digitalsln.stanserhorn.R
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Logger private constructor(private val context: Context) {

    object LogLevel {
        const val OFF = 999
        const val DEBUG = 0
        const val INFO = 1
        const val WARNING = 2
        const val ERROR = 3
        const val FATAL = 4
    }

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
        const val LOGFILE_NAME = "inCabrioLog.txt"
        private var instance: Logger? = null

        fun initialize(context: Context) {
            instance = Logger(context)
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

    @Synchronized
    private fun log(level: Int, message: String) {
        if (level < sLogLevel) return

        logToFile(level, message)
        logToConsole(level, message)
    }

    @Synchronized
    private fun log(level: Int, message: String, e: Throwable) {
        if (level < sLogLevel) return

        logToFile(level, message, e)
        logToConsole(level, message, e)

        if(level >= sLogLevelThresholdForUpload) {
            log(LogLevel.INFO, "In Logger.log(): Automatic logfile upload requested.")
            needUploadToServer = true
        }
    }

    @Synchronized
    private fun logToFile(level: Int, message: String) {
        val safeLevel = if (level == LogLevel.OFF) {
            logToFile(LogLevel.FATAL, "Log level $level invalid.");
            LogLevel.FATAL
        } else level

        val logMessage = formatLogMessage(safeLevel, message)
        try {
            val root = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val logFile = File(root, LOGFILE_NAME)
            if (!logFile.exists()) {
                logFile.createNewFile()
            }
            BufferedWriter(FileWriter(logFile, true)).use { writer ->
                writer.append(logMessage)
                writer.newLine()
            }
        } catch (e: IOException) {
            Log.e("Logger", "Error writing to log file", e)
        }
    }

    @Synchronized
    private fun logToFile(level: Int, message: String, e: Throwable) {
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

    @Synchronized
    private fun logToConsole(level: Int, message: String, e: Throwable? = null) {
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

//    fun logToConsole(level: Int, message: String, e: Throwable?) {
//        if (!sLogToAndroid) {
//            return
//        }
//        val tag = "inCabrio"
//        val logMessage = if (e == null) message else Log.getStackTraceString(e)
//        when (level) {
//            LogLevel.DEBUG -> Log.d(tag, logMessage)
//            LogLevel.INFO -> Log.i(tag, logMessage)
//            LogLevel.WARNING -> Log.w(tag, logMessage)
//            LogLevel.ERROR, LogLevel.FATAL -> Log.e(tag, logMessage)
//        }
//    }

    private fun formatLogMessage(level: Int, message: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return "[$timestamp] [$level]: $message"
    }

    @Synchronized
    fun setLogLevel(logLevel: String) {
        val correctLogLevel = convertStringToLoglevel(logLevel)
        if(sLogLevel != correctLogLevel) {
            val oldLogLevel = convertLoglevelToString(sLogLevel)
            sLogLevel = LogLevel.INFO
            log(LogLevel.INFO, "Log level changed from '$oldLogLevel' to '$logLevel'.");
            sLogLevel = correctLogLevel
        }
    }

    @Synchronized
    fun setLogLevelThreshold(logLevelThreshold: String) {
        val correctLogLevel = convertStringToLoglevel(logLevelThreshold)
        if(sLogLevel > correctLogLevel) {
            log(LogLevel.INFO, "In Logger.setLogLevelThreshold(): Found treshold for automatic uploading " +
                    "('" + logLevelThreshold + "'). Setting log level to " + logLevelThreshold + ".");
            setLogLevel(logLevelThreshold)
        }

        if (sLogLevelThresholdForUpload != correctLogLevel) {
            log(LogLevel.INFO, "Log level threshold for file upload changed from '$sLogLevelThresholdForUpload' to '$logLevelThreshold'.")
            sLogLevelThresholdForUpload = correctLogLevel
        }
    }

    private fun convertStringToLoglevel(upperLevelString: String): Int {
        val levelString = upperLevelString.toUpperCase()
        return when (levelString) {
            "DEBUG" -> LogLevel.DEBUG
            "INFO" -> LogLevel.INFO
            "WARNING" -> LogLevel.WARNING
            "ERROR" -> LogLevel.ERROR
            "FATAL" -> LogLevel.FATAL
            context.resources.getStringArray(R.array.preferences_loglevel_spinner_choices)[0].toUpperCase() -> LogLevel.OFF
            else -> {
                log(LogLevel.ERROR, "In Logger.convertStringToLoglevel(): The String '$levelString' is not a valid log level. Setting log level to INFO")
                LogLevel.INFO
            }
        }
    }

    fun convertLoglevelToString(logLevel: Int): String {
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