package com.digitalsln.stanserhorn.tools

import com.digitalsln.stanserhorn.data.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.IOException
import java.net.MalformedURLException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkHelper @Inject constructor(private val preferences: PreferenceHelper) {

    private var client: OkHttpClient = createClient()

    private fun createClient() = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(preferences.networkConnectTimeout.toLong(), TimeUnit.SECONDS)
        .readTimeout(preferences.networkReadTimeout.toLong(), TimeUnit.SECONDS)
        .build()

    suspend fun<T> fetchDataFromURL(urlString: String, parser: (String) -> T): T? {
        val request = Request.Builder()
            .url(urlString)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            try {
                if (!response.isSuccessful) {
                    return@withContext null
                }
                val responseBody = response.body?.string()
                responseBody?.let { parser(it) }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            } finally {
                response.close()
            }
        }
    }

    suspend fun<T> postToUrl(urlString: String, requestBody: RequestBody, onSuccess: (ResponseBody?) -> T): T? {
        val request: Request = Request.Builder()
            .url(urlString)
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            try {
                if (!response.isSuccessful) {
                    Logger.e("Server response was != 200 (${response.code}).")
                    return@withContext null
                }
                return@withContext onSuccess.invoke(response.body)
            } catch (e: MalformedURLException) {
                Logger.e("URL to upload '$urlString' invalid.", e)
                null
            } catch (e: IOException) {
                Logger.e("Uploading was failed ('${e.message}').", e)
                null
            }
            finally {
                response.close()
            }
        }
    }

    fun recreateClient() {
        synchronized(this) {
            val oldClient = client
            client = createClient()
            oldClient.dispatcher.cancelAll()
        }
    }
}