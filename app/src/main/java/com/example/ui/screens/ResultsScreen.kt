package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RizzReply
import com.example.data.model.ToneModifier
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun ResultsScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val replies by viewModel.generatedReplies.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeModifier by viewModel.activeModifier.collectAsState()
    val inputMessage by viewModel.inputMessage.collectAsState()

    var shareReply by remember { mutableStateOf<RizzReply?>(null) }

    val bestPick = replies.firstOrNull { it.isBestPick } ?: replies.firstOrNull()
    val otherReplies = replies.filter { it != bestPick }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgPrimary)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // TOP BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppDestination.HOME) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Your Rizz",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Text(
                            text = "Replying to: “${inputMessage.take(24)}${if (inputMessage.length > 24) "…" else ""}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Try Another Button
                TextButton(
                    onClick = { viewModel.generateRizz() },
                    enabled = !isGenerating
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = ElectricVioletLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Try another",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = ElectricVioletLight
                    )
                }
            }
        }

        // BEST PICK CARD (Dominant top card)
        if (bestPick != null) {
            item {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(text = "⭐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BEST PICK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = ElectricVioletLight
                        )
                    }

                    ResultCard(
                        reply = bestPick,
                        onCopy = { viewModel.copyToClipboard(bestPick.text) },
                        onSave = { viewModel.saveToFavorites(bestPick) },
                        onShare = { shareReply = bestPick },
                        onRefineBolder = { viewModel.refineReplies(ToneModifier.MORE_RIZZ) }
                    )
                }
            }
        }

        // MICRO-ACTION BAR ("Make it...")
        item {
            Column {
                Text(
                    text = "Make it…",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(ToneModifier.values()) { modifierItem ->
                        val isSelected = modifierItem == activeModifier
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { viewModel.refineReplies(modifierItem) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (isSelected) ElectricViolet else DarkSurfaceTertiary,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) ElectricVioletLight else Color(0x22FFFFFF)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = modifierItem.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = modifierItem.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // MORE OPTIONS HEADER & CARDS
        if (otherReplies.isNotEmpty()) {
            item {
                Text(
                    text = "More options",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }

            items(otherReplies) { reply ->
                ResultCard(
                    reply = reply,
                    onCopy = { viewModel.copyToClipboard(reply.text) },
                    onSave = { viewModel.saveToFavorites(reply) },
                    onShare = { shareReply = reply }
                )
            }
        }

        // RIZZ SCORE BREAKDOWN (Using best pick)
        if (bestPick != null) {
            item {
                RizzScoreCard(reply = bestPick)
            }
        }

        // REGENERATE ACTION BUTTON
        item {
            OutlinedButton(
                onClick = { viewModel.generateRizz() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0x338B5CF6))
            ) {
                Icon(
                    imageVector = Icons.Default.Autorenew,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Try another set of replies",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = TextWhite
                )
            }
        }
    }

    // Share Card Dialog
    shareReply?.let { replyToShare ->
        ShareCardDialog(
            reply = replyToShare,
            onDismiss = { shareReply = null }
        )
    }
}
