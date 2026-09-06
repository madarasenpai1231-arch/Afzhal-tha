package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingToast
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

data class BottomNavItem(
    val destination: AppDestination,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun MainAppScaffold(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val toast by viewModel.toast.collectAsState()

    // Handle back button press
    BackHandler(enabled = currentScreen != AppDestination.HOME && currentScreen != AppDestination.ONBOARDING) {
        viewModel.navigateTo(AppDestination.HOME)
    }

    val navItems = listOf(
        BottomNavItem(
            destination = AppDestination.HOME,
            label = "Rizz",
            selectedIcon = Icons.Filled.Bolt,
            unselectedIcon = Icons.Outlined.Bolt
        ),
        BottomNavItem(
            destination = AppDestination.ANALYZER,
            label = "Analyze",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        ),
        BottomNavItem(
            destination = AppDestination.STARTERS,
            label = "Starters",
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline
        ),
        BottomNavItem(
            destination = AppDestination.BATTLE,
            label = "Battle",
            selectedIcon = Icons.Filled.SportsEsports,
            unselectedIcon = Icons.Outlined.SportsEsports
        ),
        BottomNavItem(
            destination = AppDestination.HISTORY,
            label = "Vault",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder
        )
    )

    val showBottomBar = currentScreen != AppDestination.ONBOARDING

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBgPrimary,
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBgPrimary,
                    border = BorderStroke(1.dp, Color(0x1AFFFFFF))
                ) {
                    NavigationBar(
                        containerColor = DarkBgPrimary,
                        tonalElevation = 0.dp,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentScreen == item.destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = ElectricVioletLight,
                                    indicatorColor = ElectricViolet,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { target ->
                when (target) {
                    AppDestination.HOME -> HomeScreen(viewModel)
                    AppDestination.SCREENSHOT_RIZZ -> ScreenshotRizzScreen(viewModel)
                    AppDestination.RIZZ -> HomeScreen(viewModel)
                    AppDestination.RESULTS -> ResultsScreen(viewModel)
                    AppDestination.ANALYZER -> AnalyzerScreen(viewModel)
                    AppDestination.WHAT_THEY_MEAN -> WhatDoTheyMeanScreen(viewModel)
                    AppDestination.SHOULD_I_SEND -> ShouldISendThisScreen(viewModel)
                    AppDestination.STARTERS -> StartersScreen(viewModel)
                    AppDestination.CPR_RESCUE -> RescueScreen(viewModel)
                    AppDestination.BATTLE -> BattleScreen(viewModel)
                    AppDestination.HISTORY -> HistoryVaultScreen(viewModel)
                    AppDestination.PROFILE -> ProfileScreen(viewModel)
                    AppDestination.SUBSCRIPTION -> SubscriptionScreen(viewModel)
                    AppDestination.ONBOARDING -> OnboardingScreen(viewModel)
                }
            }

            // Global Limit Reached Dialog
            val showLimitDialog by viewModel.showLimitReachedDialog.collectAsState()
            if (showLimitDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissLimitDialog() },
                    containerColor = DarkCardElevated,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Limit Reached",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                ),
                                color = TextWhite
                            )
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "You've used all ${userProfile.dailyLimit} generations for today on your ${userProfile.selectedPlan} plan.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Upgrade to Pro to unlock 75 generations daily, high-capacity Screenshot Rizz, and deeper conversation intelligence.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.dismissLimitDialog()
                                viewModel.navigateTo(AppDestination.SUBSCRIPTION)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                        ) {
                            Text("Upgrade Now 👑", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.dismissLimitDialog() }) {
                            Text("Maybe Later", color = TextSecondary)
                        }
                    }
                )
            }

            // Floating Toast notification
            toast?.let { currentToast ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    FloatingToast(
                        toast = currentToast.message,
                        icon = currentToast.icon
                    )
                }
            }
        }
    }
}
