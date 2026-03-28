package com.dcmoote.inkwell.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dcmoote.inkwell.data.local.dao.PromptDao
import com.dcmoote.inkwell.data.local.entity.Prompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(private val promptDao: PromptDao) : ViewModel() {
    val prompts: Flow<List<Prompt>> = promptDao.getAllPrompts()

    fun deletePrompt(prompt: Prompt) {
        viewModelScope.launch { promptDao.delete(prompt) }
    }

    fun restorePrompt(prompt: Prompt) {
        viewModelScope.launch { promptDao.insert(prompt) }
    }

    class Factory(private val promptDao: PromptDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HistoryViewModel(promptDao) as T
    }
}
