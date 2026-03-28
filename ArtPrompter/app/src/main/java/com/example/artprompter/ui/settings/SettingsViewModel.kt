package com.dcmoote.inkwell.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dcmoote.inkwell.data.local.dao.PromptDao
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.dcmoote.inkwell.util.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefs: UserPreferencesManager,
    private val promptDao: PromptDao,
    private val context: Context
) : ViewModel() {

    data class State(
        val creativeType: String,
        val writingGenres: Set<String>,
        val artMediums: Set<String>,
        val artSubjects: Set<String>,
        val artThemes: Set<String>,
        val directionLevel: Int,
        val reminderEnabled: Boolean,
        val reminderHour: Int,
        val reminderMinute: Int,
        val useAi: Boolean,
        val themeMode: String
    )

    private val _state = MutableStateFlow(
        State(
            creativeType = prefs.creativeType,
            writingGenres = prefs.writingGenres,
            artMediums = prefs.artMediums,
            artSubjects = prefs.artSubjects,
            artThemes = prefs.artThemes,
            directionLevel = prefs.directionLevel,
            reminderEnabled = prefs.reminderEnabled,
            reminderHour = prefs.reminderTimeHour,
            reminderMinute = prefs.reminderTimeMinute,
            useAi = prefs.useAiWhenAvailable,
            themeMode = prefs.themeMode
        )
    )
    val state: StateFlow<State> = _state.asStateFlow()

    fun setCreativeType(type: String) {
        prefs.creativeType = type
        _state.update { it.copy(creativeType = type) }
    }

    fun toggleWritingGenre(genre: String) {
        val updated = prefs.writingGenres.toMutableSet().apply {
            if (!add(genre)) remove(genre)
        }
        prefs.writingGenres = updated
        _state.update { it.copy(writingGenres = updated) }
    }

    fun toggleArtMedium(medium: String) {
        val updated = prefs.artMediums.toMutableSet().apply {
            if (!add(medium)) remove(medium)
        }
        prefs.artMediums = updated
        _state.update { it.copy(artMediums = updated) }
    }

    fun toggleArtSubject(subject: String) {
        val updated = prefs.artSubjects.toMutableSet().apply {
            if (!add(subject)) remove(subject)
        }
        prefs.artSubjects = updated
        _state.update { it.copy(artSubjects = updated) }
    }

    fun toggleArtTheme(theme: String) {
        val updated = prefs.artThemes.toMutableSet().apply {
            if (!add(theme)) remove(theme)
        }
        prefs.artThemes = updated
        _state.update { it.copy(artThemes = updated) }
    }

    fun setDirectionLevel(level: Int) {
        prefs.directionLevel = level
        _state.update { it.copy(directionLevel = level) }
    }

    fun setReminderEnabled(enabled: Boolean) {
        prefs.reminderEnabled = enabled
        _state.update { it.copy(reminderEnabled = enabled) }
        rescheduleReminder()
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.reminderTimeHour = hour
        prefs.reminderTimeMinute = minute
        _state.update { it.copy(reminderHour = hour, reminderMinute = minute) }
        rescheduleReminder()
    }

    private fun rescheduleReminder() {
        if (!prefs.reminderEnabled) {
            ReminderScheduler.cancel(context)
            return
        }
        ReminderScheduler.schedule(context, prefs.reminderTimeHour, prefs.reminderTimeMinute)
    }

    fun setUseAi(use: Boolean) {
        prefs.useAiWhenAvailable = use
        _state.update { it.copy(useAi = use) }
    }

    fun setThemeMode(mode: String) {
        prefs.themeMode = mode
        _state.update { it.copy(themeMode = mode) }
    }

    fun clearHistory() {
        viewModelScope.launch { promptDao.deleteAll() }
    }

    class Factory(
        private val prefs: UserPreferencesManager,
        private val promptDao: PromptDao,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(prefs, promptDao, context) as T
    }
}
