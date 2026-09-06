package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RizzIntensity
import com.example.data.model.Vibe
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun HomeScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val input by viewModel.inputMessage.collectAsState()
    val selectedVibe by viewModel.selectedVibe.collectAsState()
    val selectedIntensity by viewModel.selectedIntensity.collectAsState()
    val relationshipContext by viewModel.relationshipContext.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val loadingText by viewModel.loadingText.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val recentHistory by viewModel.recentHistory.collectAsState()

    var showContextDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgPrimary)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // TOP HEADER
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RizzX",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "⚡", fontSize = 20.sp)
                    }
                    Text(
                        text = "Your conversation cheat code.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    UsageIndicatorBar(
                        used = userProfile.dailyUsageCount,
                        total = userProfile.dailyLimit,
                        onClick = { viewModel.navigateTo(AppDestination.SUBSCRIPTION) }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.navigateTo(AppDestination.PROFILE) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceTertiary)
                    ) {
                        Text(text = "😎", fontSize = 18.sp)
                    }
                }
            }
        }

        // DAILY CHALLENGE CARD
        if (!userProfile.dailyChallengeDone) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                    border = BorderStroke(1.dp, Color(0x33FF4D8D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "🔥", fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Today's Challenge",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = WarmPink.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "${userProfile.streakDays} day streak",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = WarmPinkLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = "“Turn a boring reply into something memorable.”",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.setInput("what are you doing tonight?")
                                viewModel.completeDailyChallenge()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarmPink),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Try it",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // PRIMARY OPTIONS HERO SECTION
        item {
            Column {
                Text(
                    text = "Get your next reply",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose how you want RizzX to help you right now.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // PRIMARY OPTION 1: UPLOAD SCREENSHOT CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { viewModel.navigateTo(AppDestination.SCREENSHOT_RIZZ) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ElectricViolet, WarmPink)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(ElectricViolet.copy(alpha = 0.3f), WarmPink.copy(alpha = 0.2f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📸", fontSize = 26.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Screenshot Rizz",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = WarmPink.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "HOT",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = WarmPinkLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Drop a chat screenshot. We'll read the whole vibe & write 5 replies.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    FilledTonalButton(
                        onClick = { viewModel.navigateTo(AppDestination.SCREENSHOT_RIZZ) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = ElectricViolet.copy(alpha = 0.3f))
                    ) {
                        Text("Upload", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // PRIMARY OPTION 2: PASTE OR TYPE MESSAGE
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x22FFFFFF))
                Text(
                    text = "  OR TYPE / PASTE  ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0x22FFFFFF))
            }
        }

        // LARGE INPUT CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                border = BorderStroke(1.5.dp, Color(0x338B5CF6))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Context Pill & Actions Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { showContextDialog = true },
                            shape = RoundedCornerShape(999.dp),
                            color = ElectricViolet.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "To: $relationshipContext",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElectricVioletLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = ElectricVioletLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Quick Paste Button
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = clipboard.primaryClip
                                    if (clip != null && clip.itemCount > 0) {
                                        val pasted = clip.getItemAt(0).text?.toString() ?: ""
                                        if (pasted.isNotBlank()) {
                                            viewModel.setInput(pasted)
                                            viewModel.showToast("Pasted from clipboard", "📋")
                                        }
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = ElectricVioletLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Paste",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricVioletLight
                                )
                            }

                            if (input.isNotBlank()) {
                                IconButton(
                                    onClick = { viewModel.setInput("") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Field
                    OutlinedTextField(
                        value = input,
                        onValueChange = { viewModel.setInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        placeholder = {
                            Text(
                                text = "Paste their message here… (e.g. “hey wyd?”)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = ElectricViolet
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp)
                    )
                }
            }
        }

        // PRIMARY GENERATE CTA
        item {
            RizzButton(
                text = "✨ Generate Rizz",
                isLoading = isGenerating,
                loadingText = loadingText,
                onClick = { viewModel.generateRizz() }
            )
        }

        // QUICK MODE SELECTOR
        item {
            Column {
                Text(
                    text = "Pick the Vibe",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(Vibe.values()) { vibe ->
                        ModePill(
                            vibe = vibe,
                            isSelected = vibe == selectedVibe,
                            onSelect = { viewModel.setVibe(vibe) }
                        )
                    }
                }
            }
        }

        // RIZZ INTENSITY (If Rizz vibe is selected)
        if (selectedVibe == Vibe.RIZZ) {
            item {
                RizzIntensitySlider(
                    current = selectedIntensity,
                    onLevelSelected = { viewModel.setIntensity(it) }
                )
            }
        }

        // QUICK MOVES SECTION
        item {
            Column {
                Text(
                    text = "Quick Moves",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        title = "Read Between Lines",
                        subtitle = "What do they mean?",
                        emoji = "🔍",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppDestination.WHAT_THEY_MEAN) }
                    )
                    QuickToolCard(
                        title = "Conversation CPR",
                        subtitle = "Revive dead chats 🫀",
                        emoji = "🫀",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppDestination.CPR_RESCUE) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickToolCard(
                        title = "Starters",
                        subtitle = "No more 'hey wyd?'",
                        emoji = "🚀",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppDestination.STARTERS) }
                    )
                    QuickToolCard(
                        title = "Should I Send?",
                        subtitle = "Pre-send cringe check",
                        emoji = "🛡️",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppDestination.SHOULD_I_SEND) }
                    )
                }
            }
        }

        // RECENT RIZZ VAULT PREVIEW
        if (recentHistory.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Rizz",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        TextButton(onClick = { viewModel.navigateTo(AppDestination.HISTORY) }) {
                            Text(
                                text = "View Vault →",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricVioletLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    recentHistory.take(2).forEach { item ->
                        GlassCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "“${item.replyText}”",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = TextWhite,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Replying to: ${item.inputText.ifBlank { "Message" }} • ${item.vibe}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.copyToClipboard(item.replyText) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = ElectricVioletLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Relationship Context Dialog
    if (showContextDialog) {
        AlertDialog(
            onDismissRequest = { showContextDialog = false },
            title = {
                Text(
                    text = "Who are you replying to?",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Crush", "Date", "Partner", "Friend", "Stranger", "Coworker", "Someone new").forEach { ctx ->
                        val isSelected = ctx == relationshipContext
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setRelationship(ctx)
                                    showContextDialog = false
                                },
                            color = if (isSelected) ElectricViolet.copy(alpha = 0.2f) else DarkSurfaceTertiary,
                            border = if (isSelected) BorderStroke(1.dp, ElectricViolet) else null
                        ) {
                            Text(
                                text = ctx,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextWhite else TextSecondary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showContextDialog = false }) {
                    Text("Done", color = ElectricVioletLight)
                }
            },
            containerColor = DarkCardElevated
        )
    }
}

@Composable
fun QuickToolCard(
    title: String,
    subtitle: String,
    emoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
