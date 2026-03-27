package com.dcmoote.inkwell.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class PromptLibrary(
    val writing: Map<String, Map<String, List<String>>>,
    val art: Map<String, Map<String, Map<String, List<String>>>>
)

class PromptLibraryLoader(context: Context) {

    private val library: PromptLibrary by lazy {
        val json = context.assets.open("prompts.json").bufferedReader().use { it.readText() }
        Gson().fromJson(json, PromptLibrary::class.java)
    }

    fun getWritingPrompts(genre: String, level: Int): List<String> =
        library.writing[genre]?.get(level.toString()) ?: emptyList()

    fun getArtPrompts(medium: String, subject: String, level: Int): List<String> =
        library.art[medium]?.get(subject)?.get(level.toString()) ?: emptyList()

    fun getAllWritingGenres(): List<String> =
        library.writing.keys.toList()

    fun getAllArtMediums(): List<String> =
        library.art.keys.toList()
}
