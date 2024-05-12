package com.digitalsln.stanserhorn.tools.ext

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.digitalsln.stanserhorn.tools.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun Context.writeToFile(logMessage: String) {
    kotlin.runCatching {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) CoroutineScope(Dispatchers.IO).launch {
            val resolver = contentResolver
            val collection = MediaStore.Files.getContentUri("external")

            // Проверяем, существует ли файл
            val selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "=?"
            val selectionArgs = arrayOf(Logger.getFileName())
            val cursor = resolver.query(collection, null, selection, selectionArgs, null)

            val uri: Uri? = if (cursor != null && cursor.moveToFirst()) {
                // Файл существует, получаем его URI
                val sad = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
                if (sad <= -1) null
                val id = cursor.getLong(sad)
                cursor.close()
                Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), "" + id)
            } else {
                // Файл не существует, создаем новый
                val values = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, Logger.getFileName())
                    put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                    }
                }
                resolver.insert(collection, values)!!
            }

            if (uri == null) return@launch

            // Добавляем текст в файл
            resolver.openOutputStream(uri, "wa")?.use { outputStream ->
                outputStream.write("$logMessage\n".toByteArray())
            }
        } else {
            val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val logFile = File(root, Logger.getFileName())
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            BufferedWriter(FileWriter(logFile, true)).use { writer ->
                writer.append(logMessage)
                writer.newLine()
            }
        }
    }.onFailure {
        Log.e("Logger", "Error writing to log file", it)
    }.getOrNull()
}