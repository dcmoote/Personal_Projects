package com.example.artprompter.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artprompter.ArtPrompterApplication
import com.example.artprompter.data.prefs.UserPreferencesManager
import com.example.artprompter.ui.AppViewModel
import com.example.artprompter.ui.onboarding.TimePickerDialog
import com.example.artprompter.ui.onboarding.formatTime

private val WRITING_GENRES = listOf("Fantasy", "Sci-Fi", "Horror", "Romance", "Mystery", "Thriller")
private val ART_MEDIUMS = listOf(
    "Sketch", "Watercolor", "Digital", "Oil", "Acrylic", "Ink", "Pixel Art", "Mixed Media"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(appViewModel: AppViewModel) {
    val container = (LocalContext.current.applicationContext as ArtPrompterApplication).container
    val vm: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(container.userPreferencesManager, container.promptDao)
    )
    val state by vm.state.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Settings") })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Creative preferences
            SectionHeader("Creative preferences")
            Spacer(Modifier.height(8.dp))
            Text("I create", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    UserPreferencesManager.CreativeType.WRITING to "Writing",
                    UserPreferencesManager.CreativeType.ART to "Art",
                    UserPreferencesManager.CreativeType.BOTH to "Both"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = state.creativeType == value,
                        onClick = { vm.setCreativeType(value) },
                        label = { Text(label) }
                    )
                }
            }

            if (state.creativeType != UserPreferencesManager.CreativeType.ART) {
                Spacer(Modifier.height(16.dp))
                Text("Writing genres", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WRITING_GENRES.forEach { genre ->
                        FilterChip(
                            selected = genre in state.writingGenres,
                            onClick = { vm.toggleWritingGenre(genre) },
                            label = { Text(genre) }
                        )
                    }
                }
            }

            if (state.creativeType != UserPreferencesManager.CreativeType.WRITING) {
                Spacer(Modifier.height(16.dp))
                Text("Art mediums", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ART_MEDIUMS.forEach { medium ->
                        FilterChip(
                            selected = medium in state.artMediums,
                            onClick = { vm.toggleArtMedium(medium) },
                            label = { Text(medium) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Art subject", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        UserPreferencesManager.ArtSubject.PEOPLE to "People & Portraits",
                        UserPreferencesManager.ArtSubject.LANDSCAPES to "Landscapes & Nature",
                        UserPreferencesManager.ArtSubject.BOTH to "Both"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = state.artSubject == value,
                            onClick = { vm.setArtSubject(value) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // Reminder
            SectionHeader("Daily reminder")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Remind me daily", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = state.reminderEnabled, onCheckedChange = vm::setReminderEnabled)
            }
            if (state.reminderEnabled) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reminder time: ${formatTime(state.reminderHour, state.reminderMinute)}")
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // App settings
            SectionHeader("App")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Use AI prompts when online", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Falls back to local library offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = state.useAi, onCheckedChange = vm::setUseAi)
            }

            Spacer(Modifier.height(16.dp))
            Text("Appearance", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    UserPreferencesManager.ThemeMode.LIGHT to "Light",
                    UserPreferencesManager.ThemeMode.DARK to "Dark",
                    UserPreferencesManager.ThemeMode.SYSTEM to "System"
                ).forEach { (value, label) ->
                    FilterChip(
                        selected = state.themeMode == value,
                        onClick = {
                            vm.setThemeMode(value)
                            appViewModel.setThemeMode(value)
                        },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showClearHistoryDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear prompt history", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = state.reminderHour,
            initialMinute = state.reminderMinute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m ->
                vm.setReminderTime(h, m)
                showTimePicker = false
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear history?") },
            text = { Text("All saved prompts will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearHistory()
                    showClearHistoryDialog = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}
