package com.dcmoote.inkwell.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dcmoote.inkwell.InkwellApplication
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager

private val WRITING_GENRES = listOf(
    "Fantasy", "Sci-Fi", "Horror", "Romance", "Mystery", "Thriller", "Poetry",
    "Historical Fiction", "Adventure", "Flash Fiction", "Fairy Tale", "Dystopian",
    "Humor", "Magical Realism", "Memoir"
)
private val ART_MEDIUMS = listOf(
    "Sketch", "Watercolor", "Digital", "Oil", "Acrylic", "Ink", "Pixel Art", "Mixed Media",
    "Charcoal", "Gouache", "Pastel", "Colored Pencil", "Linocut", "Marker", "Collage"
)
private val ART_SUBJECTS = listOf(
    UserPreferencesManager.ArtSubject.PEOPLE,
    UserPreferencesManager.ArtSubject.LANDSCAPES,
    UserPreferencesManager.ArtSubject.ANIMALS,
    UserPreferencesManager.ArtSubject.ABSTRACT
)
private val ART_THEMES = listOf(
    UserPreferencesManager.ArtTheme.FANTASY,
    UserPreferencesManager.ArtTheme.SCI_FI,
    UserPreferencesManager.ArtTheme.DARK_GOTHIC,
    UserPreferencesManager.ArtTheme.NATURE,
    UserPreferencesManager.ArtTheme.URBAN,
    UserPreferencesManager.ArtTheme.MYTHOLOGY,
    UserPreferencesManager.ArtTheme.SURREAL,
    UserPreferencesManager.ArtTheme.HORROR,
    UserPreferencesManager.ArtTheme.VINTAGE,
    UserPreferencesManager.ArtTheme.KAWAII
)

// Returns the ordered list of step numbers the user will visit for their creative type.
// Writing: CreativeType → WritingGenres → PromptDirection → Reminder (4 steps)
// Art:     CreativeType → ArtMedium → ArtSubject → ArtTheme → PromptDirection → Reminder (6 steps)
// Both:    CreativeType → WritingGenres → ArtMedium → ArtSubject → ArtTheme → PromptDirection → Reminder (7 steps)
private fun pathFor(creativeType: String): List<Int> = when (creativeType) {
    UserPreferencesManager.CreativeType.WRITING -> listOf(1, 2, 6, 7)
    UserPreferencesManager.CreativeType.ART -> listOf(1, 3, 4, 5, 6, 7)
    else -> listOf(1, 2, 3, 4, 5, 6, 7)
}

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val container = (LocalContext.current.applicationContext as InkwellApplication).container
    val prefs = container.userPreferencesManager
    val isPro by container.billingManager.isPro.collectAsState()
    val vm: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory(prefs))
    val state by vm.state.collectAsState()

    val path = remember(state.creativeType) { pathFor(state.creativeType) }
    val totalSteps = path.size
    fun stepIndex(n: Int) = (path.indexOf(n) + 1).coerceAtLeast(1)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (state.step) {
            0 -> WelcomeStep(onNext = { vm.advance() })
            1 -> CreativeTypeStep(
                selected = state.creativeType,
                stepIndex = stepIndex(1),
                totalSteps = totalSteps,
                onSelect = vm::setCreativeType,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            2 -> WritingGenresStep(
                selected = state.writingGenres,
                stepIndex = stepIndex(2),
                totalSteps = totalSteps,
                onToggle = vm::toggleWritingGenre,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            3 -> ArtMediumStep(
                selected = state.artMediums,
                stepIndex = stepIndex(3),
                totalSteps = totalSteps,
                onToggle = vm::toggleArtMedium,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            4 -> ArtSubjectStep(
                selected = state.artSubjects,
                stepIndex = stepIndex(4),
                totalSteps = totalSteps,
                onToggle = vm::toggleArtSubject,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            5 -> ArtThemeStep(
                selected = state.artThemes,
                stepIndex = stepIndex(5),
                totalSteps = totalSteps,
                onToggle = vm::toggleArtTheme,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            6 -> PromptDirectionStep(
                selected = state.directionLevel,
                isPro = isPro,
                stepIndex = stepIndex(6),
                totalSteps = totalSteps,
                onSelect = vm::setDirectionLevel,
                onBack = { vm.back() },
                onNext = { vm.advance() }
            )
            7 -> ReminderStep(
                enabled = state.reminderEnabled,
                hour = state.reminderHour,
                minute = state.reminderMinute,
                stepIndex = stepIndex(7),
                totalSteps = totalSteps,
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
    stepIndex: Int,
    totalSteps: Int,
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
        // Step dot indicator — reflects actual path length
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                val isCurrent = i == stepIndex
                val isPast = i < stepIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isCurrent) 10.dp else 7.dp)
                        .background(
                            color = when {
                                isPast -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                isCurrent -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(32.dp))

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
            .padding(horizontal = 28.dp)
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            text = "INKWELL",
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 3.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Your daily\ncreative\nbrief.",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(20.dp))

        // Editorial accent dash
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.secondary)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "One prompt. Tailored to your style.\nDelivered every day.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1.5f))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Get Started")
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun CreativeTypeStep(
    selected: String,
    stepIndex: Int,
    totalSteps: Int,
    onSelect: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(stepIndex = stepIndex, totalSteps = totalSteps, onBack = onBack, onNext = onNext) {
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
    stepIndex: Int,
    totalSteps: Int,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        stepIndex = stepIndex,
        totalSteps = totalSteps,
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
        MultiSelectChipGroup(options = WRITING_GENRES, selected = selected, onToggle = onToggle)
    }
}

@Composable
private fun ArtMediumStep(
    selected: Set<String>,
    stepIndex: Int,
    totalSteps: Int,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        stepIndex = stepIndex,
        totalSteps = totalSteps,
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
        MultiSelectChipGroup(options = ART_MEDIUMS, selected = selected, onToggle = onToggle)
    }
}

@Composable
private fun ArtSubjectStep(
    selected: Set<String>,
    stepIndex: Int,
    totalSteps: Int,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        stepIndex = stepIndex,
        totalSteps = totalSteps,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected.isNotEmpty()
    ) {
        Text("What do you love to draw?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Select at least one subject.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        MultiSelectChipGroup(options = ART_SUBJECTS, selected = selected, onToggle = onToggle)
    }
}

@Composable
private fun ArtThemeStep(
    selected: Set<String>,
    stepIndex: Int,
    totalSteps: Int,
    onToggle: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(
        stepIndex = stepIndex,
        totalSteps = totalSteps,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = selected.isNotEmpty()
    ) {
        Text("What themes inspire you?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Select at least one theme to guide your prompts.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        MultiSelectChipGroup(options = ART_THEMES, selected = selected, onToggle = onToggle)
    }
}

@Composable
private fun PromptDirectionStep(
    selected: Int,
    isPro: Boolean,
    stepIndex: Int,
    totalSteps: Int,
    onSelect: (Int) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    StepScaffold(stepIndex = stepIndex, totalSteps = totalSteps, onBack = onBack, onNext = onNext) {
        Text("How much guidance?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Choose how detailed you want your prompts to be.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        SelectionCard(
            title = "Minimal",
            subtitle = "A word or short phrase — just a spark",
            selected = selected == UserPreferencesManager.DirectionLevel.MINIMAL,
            onClick = { onSelect(UserPreferencesManager.DirectionLevel.MINIMAL) },
            isLocked = !isPro
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Guided",
            subtitle = "A concept with mood — you fill in the details",
            selected = selected == UserPreferencesManager.DirectionLevel.GUIDED,
            onClick = { onSelect(UserPreferencesManager.DirectionLevel.GUIDED) }
        )
        Spacer(Modifier.height(12.dp))
        SelectionCard(
            title = "Detailed",
            subtitle = "Full specifics — just pick up the brush or pen",
            selected = selected == UserPreferencesManager.DirectionLevel.DETAILED,
            onClick = { onSelect(UserPreferencesManager.DirectionLevel.DETAILED) },
            isLocked = !isPro
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderStep(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    stepIndex: Int,
    totalSteps: Int,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    StepScaffold(
        stepIndex = stepIndex,
        totalSteps = totalSteps,
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
    // TimePicker must not be placed inside AlertDialog's text slot — it causes a
    // measurement crash. Use Dialog + Surface instead to give it unconstrained space.
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set reminder time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
                TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(state.hour, state.minute) }) { Text("OK") }
                }
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    isLocked: Boolean = false
) {
    Card(
        onClick = if (isLocked) {{}} else onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = if (selected && !isLocked) 1.5.dp else 1.dp,
            color = if (selected && !isLocked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (selected && !isLocked) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = if (selected && !isLocked) 14.dp else 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected && !isLocked) FontWeight.SemiBold
                                     else FontWeight.Normal,
                        color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.width(8.dp))
                when {
                    isLocked -> Icon(
                        Icons.Default.Lock,
                        contentDescription = "Requires Pro",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    selected -> Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
