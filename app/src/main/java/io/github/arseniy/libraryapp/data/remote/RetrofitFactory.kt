package io.github.arseniy.libraryapp.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitFactory {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun createOpenLibraryApi(): OpenLibraryApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(OpenLibraryApi::class.java)
    }
}
