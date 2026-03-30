package com.dcmoote.inkwell.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

// Mirrors the structure of assets/prompts.json.
// Writing: genre → level → list of prompts
// Art:     medium → subject → level → list of prompts
data class PromptLibrary(
    val writing: Map<String, Map<String, List<String>>>,
    val art: Map<String, Map<String, Map<String, List<String>>>>
)

// Reads and parses prompts.json from the app's assets folder.
// The library is loaded lazily on first access and cached for the lifetime of the instance.
class PromptLibraryLoader(context: Context) {

    private val library: PromptLibrary by lazy {
        val json = context.assets.open("prompts.json").bufferedReader().use { it.readText() }
        Gson().fromJson(json, PromptLibrary::class.java)
    }

    // Level is an Int (1 = Minimal, 2 = Guided, 3 = Detailed) stored as a string key in the JSON.
    fun getWritingPrompts(genre: String, level: Int): List<String> =
        library.writing[genre]?.get(level.toString()) ?: emptyList()

    fun getArtPrompts(medium: String, subject: String, level: Int): List<String> =
        library.art[medium]?.get(subject)?.get(level.toString()) ?: emptyList()

    fun getAllWritingGenres(): List<String> =
        library.writing.keys.toList()

    fun getAllArtMediums(): List<String> =
        library.art.keys.toList()
}
