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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionPlan
import com.example.data.repository.RizzEngine
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun SubscriptionScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    var showPaymentFeedbackDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is com.example.data.model.PaymentState.Success -> {
                showPaymentFeedbackDialog = Pair("Subscription Active 👑", state.message)
            }
            is com.example.data.model.PaymentState.Restored -> {
                showPaymentFeedbackDialog = Pair("Purchases Restored ✓", state.message)
            }
            is com.example.data.model.PaymentState.AlreadySubscribed -> {
                showPaymentFeedbackDialog = Pair("Already Active", state.message)
            }
            is com.example.data.model.PaymentState.Failed -> {
                showPaymentFeedbackDialog = Pair("Payment Incomplete", state.message)
            }
            else -> {}
        }
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = "Upgrade RizzX",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite
                        )
                        Text(
                            text = "Unlock your full social cheat code",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    colors = ButtonDefaults.textButtonColors(contentColor = ElectricVioletLight)
                ) {
                    Text("Restore", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // HERO PITCH
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(ElectricViolet, WarmPink)))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "👑", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Level Up Your Conversation Game",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "High-capacity Screenshot Rizz, advanced conversation subtext analysis, and priority AI engine access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // PLANS LIST
        items(RizzEngine.subscriptionPlans) { plan ->
            val isCurrent = plan.id.equals(userProfile.selectedPlan, ignoreCase = true)
            PlanCard(
                plan = plan,
                isCurrent = isCurrent,
                isLoading = paymentState is com.example.data.model.PaymentState.Processing,
                onSelect = {
                    viewModel.purchasePlan(plan.id)
                }
            )
        }

        // TRANSPARENT BILLING & CANCELLATION INFO
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
                border = BorderStroke(1.dp, Color(0x1AFFFFFF))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SUBSCRIPTION TRANSPARENCY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Recurring monthly billing through Google Play.\n• Cancel anytime with zero fees directly from Google Play Store > Subscriptions.\n• Your upgraded daily limits and AI features activate immediately upon checkout.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // Payment Result Feedback Dialog
    showPaymentFeedbackDialog?.let { (title, message) ->
        AlertDialog(
            onDismissRequest = {
                showPaymentFeedbackDialog = null
                viewModel.resetPaymentState()
            },
            containerColor = DarkCardElevated,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPaymentFeedbackDialog = null
                        viewModel.resetPaymentState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun PlanCard(
    plan: SubscriptionPlan,
    isCurrent: Boolean,
    isLoading: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = when {
        isCurrent -> ElectricVioletLight
        plan.isPopular -> WarmPink
        else -> Color(0x22FFFFFF)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) DarkSurfaceSecondary else DarkCardElevated
        ),
        border = BorderStroke(if (plan.isPopular || isCurrent) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )

                    if (plan.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = WarmPink
                        ) {
                            Text(
                                text = "MOST POPULAR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = SuccessGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "CURRENT PLAN",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plan.price,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = plan.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Benefits
            plan.benefits.forEach { benefit ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (plan.isPopular) WarmPinkLight else ElectricVioletLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSelect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) DarkSurfaceTertiary else if (plan.isPopular) WarmPink else ElectricViolet
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isCurrent && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (isCurrent) "Current Plan" else if (plan.priceValue == 0) "Select Free" else "Upgrade to ${plan.name}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
