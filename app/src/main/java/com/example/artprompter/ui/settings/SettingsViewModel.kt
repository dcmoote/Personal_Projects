package com.example.artprompter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.artprompter.data.local.dao.PromptDao
import com.example.artprompter.data.prefs.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefs: UserPreferencesManager,
    private val promptDao: PromptDao
) : ViewModel() {

    data class State(
        val creativeType: String,
        val writingGenres: Set<String>,
        val artMediums: Set<String>,
        val artSubject: String,
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
            artSubject = prefs.artSubject,
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

    fun setArtSubject(subject: String) {
        prefs.artSubject = subject
        _state.update { it.copy(artSubject = subject) }
    }

    fun setReminderEnabled(enabled: Boolean) {
        prefs.reminderEnabled = enabled
        _state.update { it.copy(reminderEnabled = enabled) }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.reminderTimeHour = hour
        prefs.reminderTimeMinute = minute
        _state.update { it.copy(reminderHour = hour, reminderMinute = minute) }
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
        private val promptDao: PromptDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SettingsViewModel(prefs, promptDao) as T
    }
}
