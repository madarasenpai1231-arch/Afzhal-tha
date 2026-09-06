package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RizzEngine
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun ProfileScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val currentWingman = remember(userProfile.wingmanPersonaId) {
        RizzEngine.wingmanPersonas.firstOrNull { it.id == userProfile.wingmanPersonaId }
            ?: RizzEngine.wingmanPersonas.first()
    }

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
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Customize your AI wingman",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // USER LEVEL CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ElectricViolet, WarmPink)))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(RizzGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentWingman.emoji, fontSize = 26.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = userProfile.rankTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextWhite
                                )
                                Text(
                                    text = "Level ${userProfile.rizzLevel} • ${userProfile.selectedPlan.uppercase()} Member",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElectricVioletLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = WarmPink.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🔥", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${userProfile.streakDays}d streak",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = WarmPinkLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // XP Progress
                    val currentXp = userProfile.rizzXp % 500
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP to Level ${userProfile.rizzLevel + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "$currentXp / 500 XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { currentXp / 500f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = SparkleYellow,
                        trackColor = DarkSurfaceTertiary
                    )
                }
            }
        }

        // UPGRADE / SUBSCRIPTION TEASER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo(AppDestination.SUBSCRIPTION) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
                border = BorderStroke(1.dp, Color(0x33FFB800))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "RizzX Pro & Ultra",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = SparkleYellow
                            )
                            Text(
                                text = "Unlimited generations & priority Gemini AI",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = null,
                        tint = SparkleYellow
                    )
                }
            }
        }

        // CHOOSE WINGMAN PERSONA
        item {
            Column {
                Text(
                    text = "Active AI Wingman",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))

                RizzEngine.wingmanPersonas.forEach { persona ->
                    val isSelected = persona.id == userProfile.wingmanPersonaId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectWingman(persona.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) DarkCardElevated else DarkSurfaceTertiary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricViolet else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = persona.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = persona.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "• ${persona.tag}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricVioletLight
                                    )
                                }
                                Text(
                                    text = "“${persona.quote}”",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ElectricVioletLight,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // PREFERENCES & PHILOSOPHY
        item {
            GlassCard {
                Text(
                    text = "My Personal Style",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose your natural conversation archetype.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.data.model.UserStyle.values().take(3).forEach { style ->
                        val isSelected = style.label.equals(userProfile.userStyle, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setUserStyle(style) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricViolet else DarkSurfaceTertiary,
                            border = BorderStroke(1.dp, if (isSelected) ElectricVioletLight else Color(0x22FFFFFF))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = style.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = style.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.data.model.UserStyle.values().drop(3).take(3).forEach { style ->
                        val isSelected = style.label.equals(userProfile.userStyle, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setUserStyle(style) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricViolet else DarkSurfaceTertiary,
                            border = BorderStroke(1.dp, if (isSelected) ElectricVioletLight else Color(0x22FFFFFF))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = style.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = style.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ACCOUNT & DATA SETTINGS
        item {
            GlassCard {
                Text(
                    text = "Account & Privacy",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.restorePurchases() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restore, contentDescription = null, tint = ElectricVioletLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Restore Purchases", color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }

                HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppDestination.SUBSCRIPTION) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = SparkleYellow, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Manage Subscriptions", color = TextWhite, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(
                        text = "${userProfile.selectedPlan.uppercase()} • Manage",
                        style = MaterialTheme.typography.labelSmall.copy(color = ElectricVioletLight)
                    )
                }

                HorizontalDivider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.clearAllHistory() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = DestructiveRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Clear Vault & History", color = DestructiveRed, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("Clear", style = MaterialTheme.typography.labelSmall.copy(color = DestructiveRed))
                }
            }
        }

        // ONBOARDING RESET OPTION
        item {
            OutlinedButton(
                onClick = { viewModel.navigateTo(AppDestination.ONBOARDING) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Text("View Welcome & Onboarding Guide", color = TextSecondary)
            }
        }
    }
}
