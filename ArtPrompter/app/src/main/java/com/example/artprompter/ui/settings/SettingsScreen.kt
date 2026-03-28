package com.dcmoote.inkwell.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dcmoote.inkwell.InkwellApplication
import com.dcmoote.inkwell.data.prefs.UserPreferencesManager
import com.dcmoote.inkwell.ui.AppViewModel
import com.dcmoote.inkwell.ui.onboarding.TimePickerDialog
import com.dcmoote.inkwell.ui.onboarding.formatTime

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
private val DIRECTION_OPTIONS = listOf(
    Triple(UserPreferencesManager.DirectionLevel.MINIMAL, "Minimal", "A word or short phrase — just a spark"),
    Triple(UserPreferencesManager.DirectionLevel.GUIDED, "Guided", "A concept with mood — you fill in the details"),
    Triple(UserPreferencesManager.DirectionLevel.DETAILED, "Detailed", "Full specifics — just pick up the brush or pen")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    appViewModel: AppViewModel,
    onGoProClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val container = (context.applicationContext as InkwellApplication).container
    val isPro by container.billingManager.isPro.collectAsState()
    val vm: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(container.userPreferencesManager, container.promptDao, LocalContext.current.applicationContext)
    )
    val state by vm.state.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = { Text("Settings") })
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
                    ART_SUBJECTS.forEach { subject ->
                        FilterChip(
                            selected = subject in state.artSubjects,
                            onClick = { vm.toggleArtSubject(subject) },
                            label = { Text(subject) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Art themes", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ART_THEMES.forEach { theme ->
                        FilterChip(
                            selected = theme in state.artThemes,
                            onClick = { vm.toggleArtTheme(theme) },
                            label = { Text(theme) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Prompt direction", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                "How much guidance do you want?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            DirectionLevelSelector(
                selected = state.directionLevel,
                isPro = isPro,
                onSelect = vm::setDirectionLevel,
                onGoProClick = onGoProClick
            )

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

            // Appearance
            SectionHeader("Appearance")
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    UserPreferencesManager.ThemeMode.LIGHT to "Light",
                    UserPreferencesManager.ThemeMode.DARK to "Dark"
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

            // Prompt Library
            SectionHeader("Prompt library")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!isPro) Modifier.clickable(onClick = onGoProClick) else Modifier),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Use online library", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (isPro) "Falls back to built-in library offline"
                        else "Requires Inkwell Pro",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPro) MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.primary
                    )
                }
                if (isPro) {
                    Switch(checked = state.useAi, onCheckedChange = vm::setUseAi)
                } else {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Upgrade to Pro",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // Reset & Data
            SectionHeader("Reset & data", isWarning = true)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showClearHistoryDialog = true },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("Clear prompt history", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(24.dp))

            // About
            SectionHeader("About")
            Spacer(Modifier.height(12.dp))

            // Pro status row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (!isPro) Modifier.clickable(onClick = onGoProClick) else Modifier),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Inkwell Pro", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        if (isPro) "Unlocked" else "$6.99 one-time",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPro) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isPro) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "Upgrade",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            val versionName = remember {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Version", style = MaterialTheme.typography.bodyLarge)
                Text(versionName ?: "", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dcmoote.github.io/inkwell_privacy/"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Privacy Policy", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary)
                }
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
private fun SectionHeader(title: String, isWarning: Boolean = false) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun DirectionLevelSelector(
    selected: Int,
    isPro: Boolean,
    onSelect: (Int) -> Unit,
    onGoProClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        DIRECTION_OPTIONS.forEach { (level, label, desc) ->
            val isSelected = selected == level
            val isLocked = !isPro && level != UserPreferencesManager.DirectionLevel.GUIDED
            Card(
                onClick = { if (isLocked) onGoProClick() else onSelect(level) },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            label,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    when {
                        isLocked -> Icon(
                            Icons.Default.Lock,
                            contentDescription = "Requires Pro",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        isSelected -> Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
