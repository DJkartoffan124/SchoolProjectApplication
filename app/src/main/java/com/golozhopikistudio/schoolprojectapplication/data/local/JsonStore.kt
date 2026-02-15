package com.golozhopikistudio.schoolprojectapplication.data.local

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class JsonStore(context: Context) {

    private val file = File(context.filesDir, "app_state.json")

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        explicitNulls = false
    }

    fun load(): AppState {
        if (!file.exists()) return AppState()

        return runCatching {
            json.decodeFromString<AppState>(file.readText())
        }.getOrElse {
            AppState()
        }
    }

    fun save(state: AppState) {
        runCatching {
            file.writeText(json.encodeToString(state))
        }
    }
}
