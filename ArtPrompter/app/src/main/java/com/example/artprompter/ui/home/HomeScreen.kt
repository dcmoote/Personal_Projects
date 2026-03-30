package com.dcmoote.inkwell.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dcmoote.inkwell.InkwellApplication
import com.dcmoote.inkwell.data.local.entity.Prompt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val container = (context.applicationContext as InkwellApplication).container
    val vm: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(container.promptRepository, container.promptDao, container.userPreferencesManager, container.billingManager)
    )
    val state by vm.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingState()
            state.error != null -> ErrorState(message = state.error!!, onRetry = vm::loadTodaysPrompt)
            state.prompt != null -> PromptContent(
                prompt = state.prompt!!,
                currentStreak = state.currentStreak,
                onRefresh = vm::refreshPrompt,
                onToggleFavorite = { vm.toggleFavorite(state.prompt!!) },
                onMarkCompleted = { vm.markCompleted(state.prompt!!) }
            )
            else -> IdleState(onGenerate = vm::refreshPrompt)
        }
    }

    if (state.showTypeChooser) {
        AlertDialog(
            onDismissRequest = vm::dismissTypeChooser,
            title = { Text("What would you like today?") },
            text = { Text("Choose the type of prompt to generate.") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { vm.refreshPromptWithType("WRITING") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Writing") }
                    OutlinedButton(
                        onClick = { vm.refreshPromptWithType("ART") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Art") }
                }
            }
        )
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
            Text(
                text = "Finding your prompt…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PromptContent(
    prompt: Prompt,
    currentStreak: Int,
    onRefresh: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMarkCompleted: () -> Unit
) {
    val context = LocalContext.current
    val day = remember { SimpleDateFormat("d", Locale.getDefault()).format(Date()) }
    val month = remember { SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()).uppercase() }
    val typeLabel = remember(prompt.type, prompt.genre, prompt.subject) { promptTypeLabel(prompt) }
    val onSurface = MaterialTheme.colorScheme.onSurface
    val promptTextStyle = remember(onSurface) {
        TextStyle(
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.sp,
            color = onSurface
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ── Header ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "INKWELL",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (currentStreak > 1) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Day $currentStreak",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = month,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Prompt — editorial journal page ───────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Left accent strip — primary color bar like a journal margin rule
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 40.dp)
            ) {
                Text(
                    text = prompt.content,
                    style = promptTextStyle,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Action bar ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledActionButton(
                icon = if (prompt.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                label = if (prompt.isFavorite) "Saved" else "Save",
                onClick = onToggleFavorite,
                iconTint = if (prompt.isFavorite) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
            )
            LabeledActionButton(
                icon = Icons.Default.Check,
                label = if (prompt.isCompleted) "Created" else "Done",
                onClick = onMarkCompleted,
                iconTint = if (prompt.isCompleted) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
            )
            LabeledActionButton(
                icon = Icons.Default.Refresh,
                label = "New",
                onClick = onRefresh
            )
            LabeledActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, prompt.content)
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }
            )
        }

        // ── Source badge ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (prompt.source == "AI")
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (prompt.source == "AI") "Online library" else "Built-in library",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (prompt.source == "AI")
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LabeledActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = iconTint)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IdleState(onGenerate: () -> Unit) {
    val today = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
    ) {
        Spacer(Modifier.weight(1f))

        // Accent rule — editorial section marker
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(3.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "What will\nyou create\ntoday?",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = today,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Get My Prompt",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onRetry) { Text("Try Again") }
        }
    }
}

private fun promptTypeLabel(prompt: Prompt): String {
    return if (prompt.type == "WRITING") {
        "Writing \u2022 ${prompt.genre}"
    } else {
        val sub = if (prompt.subject != null) " \u2022 ${prompt.subject}" else ""
        "Art \u2022 ${prompt.genre}$sub"
    }
}
