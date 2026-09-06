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
import com.example.data.repository.RizzEngine
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel
import kotlinx.coroutines.delay

@Composable
fun StartersScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.selectedStarterCategory.collectAsState()
    val starters by viewModel.currentStarters.collectAsState()

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
                        text = "Conversation Starters",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "No more “hey wyd?”",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // CATEGORY SELECTOR PILLS
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(RizzEngine.starterCategories) { cat ->
                    val isSelected = cat.id == selectedCategory
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { viewModel.selectStarterCategory(cat.id) },
                        shape = RoundedCornerShape(999.dp),
                        color = if (isSelected) ElectricViolet else DarkSurfaceTertiary,
                        border = BorderStroke(1.dp, if (isSelected) ElectricVioletLight else Color(0x22FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = cat.emoji, fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.label,
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

        // CATEGORY DESCRIPTION
        item {
            val catObj = RizzEngine.starterCategories.firstOrNull { it.id == selectedCategory }
            if (catObj != null) {
                Text(
                    text = "${catObj.emoji} ${catObj.label} — ${catObj.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricVioletLight
                )
            }
        }

        // STARTERS LIST
        items(starters) { starterText ->
            StarterItemCard(
                text = starterText,
                onCopy = { viewModel.copyToClipboard(starterText) },
                onUseAsReply = {
                    viewModel.setInput(starterText)
                    viewModel.navigateTo(AppDestination.HOME)
                }
            )
        }
    }
}

@Composable
private fun StarterItemCard(
    text: String,
    onCopy: () -> Unit,
    onUseAsReply: () -> Unit
) {
    var isCopied by remember { mutableStateOf(false) }

    GlassCard {
        Text(
            text = "“$text”",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            ),
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    isCopied = true
                    onCopy()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCopied) SuccessGreen else ElectricViolet
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCopied) "Copied ✓" else "Copy",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            TextButton(onClick = onUseAsReply) {
                Text(
                    text = "Refine in Rizz →",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmPinkLight
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
}
