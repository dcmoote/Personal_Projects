package com.dcmoote.inkwell.util

import android.content.Context
import com.dcmoote.inkwell.data.local.AppDatabase
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager

class AppContainer(context: Context) {
    val userPreferencesManager = UserPreferencesManager(context)
    val promptLibraryLoader = PromptLibraryLoader(context)
    val database = AppDatabase.getInstance(context)
    val promptDao = database.promptDao()
    val promptRepository = PromptRepository(userPreferencesManager, promptDao, promptLibraryLoader)
    val billingManager = BillingManager(context)
}
