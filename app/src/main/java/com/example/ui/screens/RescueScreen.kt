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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RescueSituation
import com.example.data.repository.RizzEngine
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel
import kotlinx.coroutines.delay

@Composable
fun RescueScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSituation by viewModel.selectedRescueSituation.collectAsState()

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Conversation CPR",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🫀", fontSize = 20.sp)
                    }
                    Text(
                        text = "The conversation is dying. We can work with that.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // SITUATIONS ROW
        item {
            Column {
                Text(
                    text = "Pick the Situation",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(RizzEngine.rescueSituations) { situation ->
                        val isSelected = situation.id == selectedSituation.id
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { viewModel.selectRescueSituation(situation) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (isSelected) WarmPink else DarkSurfaceTertiary,
                            border = BorderStroke(1.dp, if (isSelected) WarmPinkLight else Color(0x22FFFFFF))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = situation.emoji, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = situation.label,
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

        // ACTIVE SITUATION HEADER
        item {
            GlassCard(border = BorderStroke(1.dp, Color(0x33FF4D8D))) {
                Text(
                    text = "${selectedSituation.emoji} ${selectedSituation.label}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedSituation.subhead,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // STRATEGY CARDS
        item {
            Text(
                text = "3 Recovery Angles",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextWhite
            )
        }

        item {
            StrategyRescueCard(
                badge = "Playful Recovery",
                badgeColor = SparkleYellow,
                quote = selectedSituation.playfulStrategy,
                explanation = "Acknowledge the lull with charming humor to reset the energy.",
                onCopy = { viewModel.copyToClipboard(selectedSituation.playfulStrategy) }
            )
        }

        item {
            StrategyRescueCard(
                badge = "Direct Recovery",
                badgeColor = ElectricVioletLight,
                quote = selectedSituation.directStrategy,
                explanation = "Shift to an authentic, interesting topic without bringing up the silence.",
                onCopy = { viewModel.copyToClipboard(selectedSituation.directStrategy) }
            )
        }

        item {
            StrategyRescueCard(
                badge = "Low-Pressure Recovery",
                badgeColor = SuccessGreen,
                quote = selectedSituation.lowPressureStrategy,
                explanation = "Leaves the door open comfortably without demanding immediate investment.",
                onCopy = { viewModel.copyToClipboard(selectedSituation.lowPressureStrategy) }
            )
        }

        // RESPECTFUL PROTOCOL FOOTER
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceTertiary
            ) {
                Text(
                    text = "Wingman Rule: If someone isn't responding after a respectful follow-up, give them space. Real charisma knows when to step back.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun StrategyRescueCard(
    badge: String,
    badgeColor: Color,
    quote: String,
    explanation: String,
    onCopy: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
        border = BorderStroke(1.dp, Color(0x22FFFFFF))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = {
                        isCopied = true
                        onCopy()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopied) SuccessGreen else ElectricViolet
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isCopied) "Copied" else "Copy",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "“$quote”",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1200)
            isCopied = false
        }
    }
}
