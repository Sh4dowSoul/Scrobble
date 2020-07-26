package de.schnettler.lastfm

import android.util.Base64
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response

fun String.encodeBase64(): String = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)

inline fun <reified T> Response<*>.parseErrJsonResponse(): T? {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val parser = moshi.adapter(T::class.java)
    val response = errorBody()?.string()
    if (response != null) {
        try {
            return parser.fromJson(response)
        } catch (e: JsonDataException) {
            e.printStackTrace()
        }
    }
    return null
}