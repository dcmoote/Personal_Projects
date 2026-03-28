package com.dcmoote.inkwell.util

import com.dcmoote.inkwell.data.local.dao.PromptDao
import com.dcmoote.inkwell.data.local.entity.Prompt
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PromptRepository(
    private val prefs: UserPreferencesManager,
    private val promptDao: PromptDao,
    private val libraryLoader: PromptLibraryLoader
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val gson = Gson()

    suspend fun getTodaysPrompt(): Prompt? {
        val latest = promptDao.getLatestPrompt() ?: return null
        val today = dateFormat.format(Date())
        val promptDate = dateFormat.format(Date(latest.timestamp))
        return if (today == promptDate) latest else null
    }

    suspend fun generateNewPrompt(typeOverride: String? = null, isPro: Boolean = false): Prompt {
        val prompt = if (isPro && prefs.useAiWhenAvailable) {
            try {
                generateAiPrompt(typeOverride)
            } catch (e: Exception) {
                Log.e("PromptRepository", "AI generation failed", e)
                generateLocalPrompt(typeOverride, isPro)
            }
        } else {
            generateLocalPrompt(typeOverride, isPro)
        }
        val insertedId = promptDao.insert(prompt)
        return prompt.copy(id = insertedId.toInt())
    }

    private suspend fun generateAiPrompt(typeOverride: String? = null): Prompt = withContext(Dispatchers.IO) {
        val type = typeOverride ?: prefs.creativeType
        val genres = prefs.writingGenres.toList()
        val mediums = prefs.artMediums.toList()
        val subjects = prefs.artSubjects.toList()
        val themes = prefs.artThemes.toList()
        val directionLevel = prefs.directionLevel

        val requestBody = gson.toJson(mapOf(
            "type" to type,
            "genres" to genres,
            "mediums" to mediums,
            "subjects" to subjects,
            "themes" to themes,
            "directionLevel" to directionLevel
        ))

        val url = URL(WORKER_URL)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 10_000
        conn.readTimeout = 20_000

        OutputStreamWriter(conn.outputStream).use { it.write(requestBody) }

        val responseCode = conn.responseCode
        if (responseCode != 200) {
            val error = conn.errorStream?.bufferedReader()?.readText() ?: "HTTP $responseCode"
            throw Exception("Worker error: $error")
        }

        val responseText = conn.inputStream.bufferedReader().readText()
        val response = gson.fromJson(responseText, Map::class.java)
        val promptText = response["prompt"] as? String
            ?: throw Exception("No prompt in response")

        // Resolve type for entity tagging
        val resolvedType = when (type) {
            UserPreferencesManager.CreativeType.WRITING -> "WRITING"
            UserPreferencesManager.CreativeType.ART -> "ART"
            else -> if (Math.random() > 0.5) "WRITING" else "ART"
        }
        val genre = genres.randomOrNull() ?: "General"
        val medium = mediums.randomOrNull() ?: "General"
        val subject = subjects.randomOrNull()

        if (resolvedType == "WRITING") {
            Prompt(type = "WRITING", genre = genre, subject = null, content = promptText.trim(), source = "AI")
        } else {
            Prompt(type = "ART", genre = medium, subject = subject, content = promptText.trim(), source = "AI")
        }
    }

    private fun generateLocalPrompt(typeOverride: String? = null, isPro: Boolean = false): Prompt {
        val type = typeOverride ?: prefs.creativeType
        val genres = prefs.writingGenres.toList()
        val mediums = prefs.artMediums.toList()
        val subjects = prefs.artSubjects.toList()
        val level = if (isPro) prefs.directionLevel else UserPreferencesManager.DirectionLevel.GUIDED

        val resolvedType = when (type) {
            UserPreferencesManager.CreativeType.WRITING -> "WRITING"
            UserPreferencesManager.CreativeType.ART -> "ART"
            else -> if (Math.random() > 0.5) "WRITING" else "ART"
        }

        return if (resolvedType == "WRITING") {
            val selectedGenre = genres.randomOrNull() ?: libraryLoader.getAllWritingGenres().random()
            val content = libraryLoader.getWritingPrompts(selectedGenre, level).randomOrNull()
                ?: "Write whatever comes to mind today."
            Prompt(type = "WRITING", genre = selectedGenre, subject = null, content = content, source = "LOCAL")
        } else {
            val selectedMedium = mediums.randomOrNull() ?: libraryLoader.getAllArtMediums().random()
            val selectedSubject = subjects.randomOrNull() ?: UserPreferencesManager.ArtSubject.PEOPLE
            val content = libraryLoader.getArtPrompts(selectedMedium, selectedSubject, level).randomOrNull()
                ?: "Sketch something you see right now."
            Prompt(type = "ART", genre = selectedMedium, subject = selectedSubject, content = content, source = "LOCAL")
        }
    }

    companion object {
        const val WORKER_URL = "https://art-prompter-ai.dcmoote.workers.dev"
    }
}
