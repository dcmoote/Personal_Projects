package com.example.artprompter.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.artprompter.data.prefs.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(private val prefs: UserPreferencesManager) : ViewModel() {

    data class State(
        val step: Int = 0,
        val creativeType: String = UserPreferencesManager.CreativeType.BOTH,
        val writingGenres: Set<String> = emptySet(),
        val artMediums: Set<String> = emptySet(),
        val artSubject: String = UserPreferencesManager.ArtSubject.BOTH,
        val reminderEnabled: Boolean = false,
        val reminderHour: Int = 9,
        val reminderMinute: Int = 0
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun setCreativeType(type: String) = _state.update { it.copy(creativeType = type) }

    fun toggleWritingGenre(genre: String) = _state.update {
        val updated = it.writingGenres.toMutableSet().apply {
            if (!add(genre)) remove(genre)
        }
        it.copy(writingGenres = updated)
    }

    fun toggleArtMedium(medium: String) = _state.update {
        val updated = it.artMediums.toMutableSet().apply {
            if (!add(medium)) remove(medium)
        }
        it.copy(artMediums = updated)
    }

    fun setArtSubject(subject: String) = _state.update { it.copy(artSubject = subject) }
    fun setReminderEnabled(enabled: Boolean) = _state.update { it.copy(reminderEnabled = enabled) }
    fun setReminderTime(hour: Int, minute: Int) =
        _state.update { it.copy(reminderHour = hour, reminderMinute = minute) }

    // Step 0: Welcome, 1: CreativeType, 2: WritingGenres, 3: ArtMedium, 4: ArtSubject, 5: Reminder
    fun advance() {
        val s = _state.value
        val next = when (s.step) {
            1 -> if (s.creativeType == UserPreferencesManager.CreativeType.ART) 3 else 2
            2 -> if (s.creativeType == UserPreferencesManager.CreativeType.WRITING) 5 else 3
            else -> s.step + 1
        }
        _state.update { it.copy(step = next) }
    }

    fun back() {
        val s = _state.value
        val prev = when (s.step) {
            3 -> if (s.creativeType == UserPreferencesManager.CreativeType.ART) 1 else 2
            5 -> if (s.creativeType == UserPreferencesManager.CreativeType.WRITING) 2 else 4
            else -> s.step - 1
        }
        _state.update { it.copy(step = prev) }
    }

    fun completeOnboarding() {
        val s = _state.value
        with(prefs) {
            creativeType = s.creativeType
            writingGenres = s.writingGenres
            artMediums = s.artMediums
            artSubject = s.artSubject
            reminderEnabled = s.reminderEnabled
            reminderTimeHour = s.reminderHour
            reminderTimeMinute = s.reminderMinute
            onboardingComplete = true
        }
    }

    class Factory(private val prefs: UserPreferencesManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            OnboardingViewModel(prefs) as T
    }
}
