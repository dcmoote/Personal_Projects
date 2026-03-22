package com.example.artprompter.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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

private val WRITING_GENRES = listOf("Fantasy", "Sci-Fi", "Horror", "Romance", "Mystery", "Thriller")
private val ART_MEDIUMS = listOf(
    "Sketch", "Watercolor", "Digital", "Oil", "Acrylic", "Ink", "Pixel Art", "Mixed Media"
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val prefs = (LocalContext.current.applicationContext as ArtPrompterApplication)
        .container.userPreferencesManager
    val vm: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory(prefs))
    val state by vm.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.step) {
            0 -> WelcomeStep(onNext = { vm.advance() })
            1 -> CreativeTypeStep(
                selected = state.creativeType,
                onSelect = vm::setCreativeType,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            2 -> WritingGenresStep(
                selected = state.writingGenres,
                onToggle = vm::toggleWritingGenre,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            3 -> ArtMediumStep(
                selected = state.artMediums,
                onToggle = vm::toggleArtMedium,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            4 -> ArtSubjectStep(
                selected = state.artSubject,
                onSelect = vm::setArtSubject,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            5 -> ReminderStep(
                enabled = state.reminderEnabled,
                hour = state.reminderHour,
                minute = state.reminderMinute,
                onToggle = vm::setReminderEnabled,
                onTimeChange = vm::setReminderTime,
                onBack = { vm.back() },
                onComplete = {
                    vm.completeOnboarding()
                    onComplete()
                }
            )
        }
    }
}

@Composable
private fun StepScaffold(
    step: Int,
    onBack: (() -> Unit)?,
    nextLabel: String = "Next",
    onNext: () -> Unit,
    nextEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        if (step > 0) {
            LinearProgressIndicator(
                progress = { step / 5f },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            content()
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                OutlinedButton(onClick = onBack) { Text("Back") }
            } else {
                Spacer(Modifier.width(1.dp))
            }
            Button(onClick = onNext, enabled = nextEnabled) {
                Text(nextLabel)
            }
        }
    }
}

@Composable
private fun WelcomeStep(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Let's set up your\ncreative space",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Personalized prompts tailored to your creative style, delivered daily.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Get Started")
        }
    }
}

@Composable
private fun CreativeTypeStep(
    selected: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(step = 1, onBack = onBack, onNext = onNext) {
        Text("What do you create?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Choose one to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        SelectionCard(
            title = "Writing",
            subtitle = "Short stories, poetry, fiction prompts",
            selected = selected == UserPreferencesManager.CreativeType.WRITING,
            onClick = { onSelect(UserPreferencesManager.CreativeType.WRITING) }
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Art",
            subtitle = "Drawing, painting, and visual work",
            selected = selected == UserPreferencesManager.CreativeType.ART,
            onClick = { onSelect(UserPreferencesManager.CreativeType.ART) }
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Both",
            subtitle = "Mix of writing and art prompts",
            selected = selected == UserPreferencesManager.CreativeType.BOTH,
            onClick = { onSelect(UserPreferencesManager.CreativeType.BOTH) }
        )
    }
}

@Composable
private fun WritingGenresStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        step = 2,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected.isNotEmpty()
    ) {
        Text("What do you like to write?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Select at least one genre.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        MultiSelectChipGroup(
            options = WRITING_GENRES,
            selected = selected,
            onToggle = onToggle
        )
    }
}

@Composable
private fun ArtMediumStep(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        step = 3,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected.isNotEmpty()
    ) {
        Text("What medium do you work in?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Select at least one.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        MultiSelectChipGroup(
            options = ART_MEDIUMS,
            selected = selected,
            onToggle = onToggle
        )
    }
}

@Composable
private fun ArtSubjectStep(
    selected: String,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(step = 4, onBack = onBack, onNext = onNext) {
        Text("What do you love to draw?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Choose your subject focus.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        SelectionCard(
            title = "People & Portraits",
            subtitle = "Figures, faces, and character studies",
            selected = selected == UserPreferencesManager.ArtSubject.PEOPLE,
            onClick = { onSelect(UserPreferencesManager.ArtSubject.PEOPLE) }
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Landscapes & Nature",
            subtitle = "Environments, scenery, and the natural world",
            selected = selected == UserPreferencesManager.ArtSubject.LANDSCAPES,
            onClick = { onSelect(UserPreferencesManager.ArtSubject.LANDSCAPES) }
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Both",
            subtitle = "A mix of subjects",
            selected = selected == UserPreferencesManager.ArtSubject.BOTH,
            onClick = { onSelect(UserPreferencesManager.ArtSubject.BOTH) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderStep(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    StepScaffold(
        step = 5,
        onBack = onBack,
        nextLabel = "Finish",
        onNext = onComplete
    ) {
        Text("Daily reminder", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Get a nudge each day to create something.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Remind me daily", style = MaterialTheme.typography.titleMedium)
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
        if (enabled) {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reminder time: ${formatTime(hour, minute)}")
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = { showTimePicker = false },
            onConfirm = { h, m ->
                onTimeChange(h, m)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set reminder time") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun MultiSelectChipGroup(
    options: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option in selected,
                onClick = { onToggle(option) },
                label = { Text(option) }
            )
        }
    }
}

fun formatTime(hour: Int, minute: Int): String {
    val h = if (hour == 0 || hour == 12) 12 else hour % 12
    val amPm = if (hour < 12) "AM" else "PM"
    return "%d:%02d %s".format(h, minute, amPm)
}
