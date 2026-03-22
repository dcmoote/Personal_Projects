package com.example.artprompter.util

import android.content.Context
import com.example.artprompter.data.local.AppDatabase
import com.example.artprompter.data.prefs.UserPreferencesManager

class AppContainer(context: Context) {
    val userPreferencesManager = UserPreferencesManager(context)
    val promptLibraryLoader = PromptLibraryLoader(context)
    val database = AppDatabase.getInstance(context)
    val promptDao = database.promptDao()
}
