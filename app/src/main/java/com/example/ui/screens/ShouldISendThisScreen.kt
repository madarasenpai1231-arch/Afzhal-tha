package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.RizzButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun ShouldISendThisScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val input by viewModel.shouldISendInput.collectAsState()
    val isAuditing by viewModel.isAuditing.collectAsState()
    val result by viewModel.auditResult.collectAsState()

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
                        text = "Should I Send This?",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Pre-send cringe risk and confidence audit",
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
                    text = "Your Draft Message",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.shouldISendInput.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    placeholder = {
                        Text(
                            text = "Paste the message you're overthinking right now…",
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
                    text = "Check This",
                    isLoading = isAuditing,
                    loadingText = "Auditing vibe & cringe…",
                    onClick = { viewModel.runPreSendAudit() }
                )
            }
        }

        // AUDIT RESULTS
        if (result != null) {
            val audit = result!!

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
                    border = BorderStroke(1.dp, Color(0x338B5CF6))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Audit Verdict",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“${audit.verdict}”",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricVioletLight
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AuditMetricRow("Confidence", audit.confidenceScore, SuccessGreen)
                        AuditMetricRow("Naturalness", audit.naturalnessScore, SparkleYellow)
                        AuditMetricRow("Cringe Risk", audit.cringeRiskScore, DestructiveRed)
                        AuditMetricRow("Flirt Level", audit.flirtLevelScore, WarmPinkLight)
                        AuditMetricRow("Pressure Level", audit.pressureLevelScore, WarningAmber)

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Entertainment estimates to help you avoid overthinking.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // FIXED VERSION CARD ("Fix it")
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(ElectricViolet, WarmPink)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = null,
                                    tint = SparkleYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Polished & Less Cringe",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricVioletLight
                                    )
                                )
                            }

                            IconButton(
                                onClick = { viewModel.copyToClipboard(audit.fixedVersion) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“${audit.fixedVersion}”",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.copyToClipboard(audit.fixedVersion) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Copy Polished Reply ✓")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditMetricRow(label: String, score: Int, color: Color) {
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
            modifier = Modifier.width(110.dp)
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
