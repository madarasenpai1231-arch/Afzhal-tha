package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.Vibe
import com.example.ui.components.GlassCard
import com.example.ui.components.RizzButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun AnalyzerScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val input by viewModel.analyzerInput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val result by viewModel.analysisResult.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgPrimary)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // TOP BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppDestination.HOME) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Conversation Analyzer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Read the vibe and understand the flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // INPUT CARD
        item {
            GlassCard {
                Text(
                    text = "Paste Conversation",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Drop a snippet of your recent back-and-forth messages.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.analyzerInput.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp),
                    placeholder = {
                        Text(
                            text = "Them: hey what are you doing?\nMe: just studying, you?\nThem: same lol so boring",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                RizzButton(
                    text = "Analyze Conversation",
                    isLoading = isAnalyzing,
                    loadingText = "Reading the chemistry…",
                    onClick = { viewModel.analyzeConversation() }
                )
            }
        }

        // RESULTS
        if (result != null) {
            val analysis = result!!

            item {
                Text(
                    text = "Vibe Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }

            // Energy Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
                    border = BorderStroke(1.dp, Color(0x338B5CF6))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Conversation Energy",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite
                                )
                                Text(
                                    text = analysis.energyLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = ElectricVioletLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = "${analysis.energyScore}%",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = WarmPinkLight
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        VibeMetricRow("Playfulness", analysis.playfulnessScore, SparkleYellow)
                        VibeMetricRow("Question Balance", analysis.questionBalanceScore, ElectricVioletLight)
                        VibeMetricRow("Momentum", analysis.momentumScore, SuccessGreen)
                        VibeMetricRow("Awkwardness", analysis.awkwardnessScore, WarningAmber)

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "AI-generated estimates for guidance and entertainment, not scientific measurements.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // Insights
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    InsightCard(
                        title = "What's Working",
                        content = analysis.whatsWorking,
                        emoji = "✅",
                        borderColor = Color(0x3310B981)
                    )
                    InsightCard(
                        title = "Potential Issue",
                        content = analysis.potentialIssue,
                        emoji = "⚠️",
                        borderColor = Color(0x33F59E0B)
                    )
                    InsightCard(
                        title = "Try This",
                        content = analysis.tryThis,
                        emoji = "💡",
                        borderColor = Color(0x338B5CF6)
                    )
                }
            }

            // Next Move Button
            item {
                Button(
                    onClick = {
                        viewModel.setInput(analysis.recommendedNextMessage)
                        viewModel.navigateTo(AppDestination.HOME)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Give me the next message",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun VibeMetricRow(label: String, score: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(130.dp)
        )
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = DarkSurfaceTertiary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$score%",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = TextWhite,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
private fun InsightCard(title: String, content: String, emoji: String, borderColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}
