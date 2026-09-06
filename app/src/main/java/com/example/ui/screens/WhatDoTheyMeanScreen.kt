package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.MeaningOption
import com.example.ui.components.GlassCard
import com.example.ui.components.RizzButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel
import kotlinx.coroutines.delay

@Composable
fun WhatDoTheyMeanScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val input by viewModel.meanInput.collectAsState()
    val isDecoding by viewModel.isDecoding.collectAsState()
    val result by viewModel.meaningResult.collectAsState()

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
                        text = "Read Between the Lines",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Get possible interpretations—not mind reading",
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
                    text = "What did they say?",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { viewModel.meanInput.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. “Okayyy sure 😂” or “k”", color = TextMuted) },
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
                    text = "Analyze Meaning",
                    isLoading = isDecoding,
                    loadingText = "Decoding context…",
                    onClick = { viewModel.analyzeWhatDoTheyMean() }
                )
            }
        }

        // RESULTS
        if (result != null) {
            val res = result!!

            item {
                Text(
                    text = "Possible Interpretations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }

            items(res.meanings) { option ->
                InterpretationCard(option)
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Recommended Responses",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }

            items(res.bestResponses) { replyText ->
                SuggestedReplyCard(
                    text = replyText,
                    onCopy = { viewModel.copyToClipboard(replyText) }
                )
            }
        }
    }
}

@Composable
private fun InterpretationCard(option: MeaningOption) {
    val badgeColor = when (option.indicatorColor) {
        "GREEN" -> SuccessGreen
        "YELLOW" -> SparkleYellow
        else -> WarningAmber
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
        border = BorderStroke(1.dp, Color(0x22FFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(badgeColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${option.percentage}% likely",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = option.interpretation,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SuggestedReplyCard(text: String, onCopy: () -> Unit) {
    var isCopied by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.padding(bottom = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "“$text”",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = TextWhite,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    isCopied = true
                    onCopy()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCopied) SuccessGreen else ElectricViolet
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
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

            LaunchedEffect(isCopied) {
                if (isCopied) {
                    delay(1200)
                    isCopied = false
                }
            }
        }
    }
}
