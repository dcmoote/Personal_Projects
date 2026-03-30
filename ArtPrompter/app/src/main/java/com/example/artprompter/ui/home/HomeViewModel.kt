package com.dcmoote.inkwell.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dcmoote.inkwell.data.local.dao.PromptDao
import com.dcmoote.inkwell.data.local.entity.Prompt
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.dcmoote.inkwell.util.BillingManager
import com.dcmoote.inkwell.util.PromptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// All UI state for the home screen in one place.
data class HomeUiState(
    val prompt: Prompt? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showTypeChooser: Boolean = false, // true when the user has BOTH selected and must pick a type
    val currentStreak: Int = 0
)

class HomeViewModel(
    private val repository: PromptRepository,
    private val promptDao: PromptDao,
    private val prefs: UserPreferencesManager,
    private val billingManager: BillingManager
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Restore today's prompt if one already exists — avoids generating a duplicate on relaunch.
            val existing = repository.getTodaysPrompt()
            if (existing != null) {
                _uiState.update { it.copy(prompt = existing) }
            }
            _uiState.update { it.copy(currentStreak = effectiveStreak()) }
        }
    }

    // Called when the user taps the main "Get Prompt" button.
    // If they already have one for today, shows it. If type is BOTH, shows the type chooser dialog.
    fun loadTodaysPrompt() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val existing = repository.getTodaysPrompt()
                if (existing != null) {
                    _uiState.update { it.copy(prompt = existing, isLoading = false) }
                } else if (prefs.creativeType == UserPreferencesManager.CreativeType.BOTH) {
                    _uiState.update { it.copy(isLoading = false, showTypeChooser = true) }
                } else {
                    val new = repository.generateNewPrompt(isPro = billingManager.isPro.value)
                    _uiState.update { it.copy(prompt = new, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Couldn't load a prompt. Tap refresh to try again.") }
            }
        }
    }

    // Called when the user taps the refresh/shuffle icon to get a new prompt.
    fun refreshPrompt() {
        if (prefs.creativeType == UserPreferencesManager.CreativeType.BOTH) {
            _uiState.update { it.copy(showTypeChooser = true) }
            return
        }
        generatePrompt()
    }

    // Called after the user picks a type in the chooser dialog.
    fun refreshPromptWithType(type: String) {
        _uiState.update { it.copy(showTypeChooser = false) }
        generatePrompt(typeOverride = type)
    }

    fun dismissTypeChooser() {
        _uiState.update { it.copy(showTypeChooser = false) }
    }

    private fun generatePrompt(typeOverride: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val new = repository.generateNewPrompt(typeOverride, isPro = billingManager.isPro.value)
                _uiState.update { it.copy(prompt = new, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Couldn't generate a new prompt.") }
            }
        }
    }

    fun toggleFavorite(prompt: Prompt) {
        viewModelScope.launch {
            val updated = prompt.copy(isFavorite = !prompt.isFavorite)
            promptDao.update(updated)
            _uiState.update { it.copy(prompt = updated) }
        }
    }

    // Marks the prompt as done and updates the streak. No-ops if already completed today.
    fun markCompleted(prompt: Prompt) {
        if (prompt.isCompleted) return
        viewModelScope.launch {
            val updated = prompt.copy(isCompleted = true)
            promptDao.update(updated)
            _uiState.update { it.copy(prompt = updated) }
            recordCompletion()
        }
    }

    private fun recordCompletion() {
        val today = dateFormat.format(Date())
        if (prefs.lastCompletionDate == today) return // already recorded today

        // Streak continues if yesterday was the last completion date; otherwise it resets to 1.
        val newStreak = when (prefs.lastCompletionDate) {
            yesterday() -> prefs.currentStreak + 1
            else -> 1
        }
        prefs.lastCompletionDate = today
        prefs.currentStreak = newStreak
        if (newStreak > prefs.longestStreak) prefs.longestStreak = newStreak

        _uiState.update { it.copy(currentStreak = newStreak) }
    }

    // Returns the streak the user can still extend today — resets display to 0 if they've lapsed
    private fun effectiveStreak(): Int {
        val lastDate = prefs.lastCompletionDate
        return when {
            lastDate == dateFormat.format(Date()) -> prefs.currentStreak
            lastDate == yesterday() -> prefs.currentStreak
            else -> 0
        }
    }

    private fun yesterday(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal.time)
    }

    class Factory(
        private val repository: PromptRepository,
        private val promptDao: PromptDao,
        private val prefs: UserPreferencesManager,
        private val billingManager: BillingManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, promptDao, prefs, billingManager) as T
    }
}
