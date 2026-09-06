package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.NavigateNext
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
import com.example.ui.components.RizzButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.RizzViewModel

data class OnboardingStep(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val pill: String
)

@Composable
fun OnboardingScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            emoji = "⚡",
            title = "Never get left on read again.",
            subtitle = "Turn boring 'wyd' messages and dry replies into effortless, memorable banter that keeps the conversation alive.",
            pill = "Effortless Momentum"
        ),
        OnboardingStep(
            emoji = "✨",
            title = "Natural, charismatic, non-cringe replies.",
            subtitle = "No cheesy pickup lines or robotic scripts. Observational wit and confident charm designed to feel authentically like you.",
            pill = "Zero Cringe Guarantee"
        ),
        OnboardingStep(
            emoji = "🔥",
            title = "Choose your vibe & control the heat.",
            subtitle = "From Smooth and Playful to Savage and Cold—tune your responses with single-tap modifiers to match any situation.",
            pill = "Full Social Control"
        )
    )

    val current = steps[stepIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP SKIP
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.completeOnboarding() }) {
                Text("Skip", color = TextSecondary)
            }
        }

        // CENTER ILLUSTRATION & TEXT
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated Icon Box
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ElectricViolet.copy(alpha = 0.5f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = DarkCardElevated,
                    border = BorderStroke(1.5.dp, ElectricVioletLight),
                    modifier = Modifier.size(86.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = current.emoji, fontSize = 42.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                shape = RoundedCornerShape(999.dp),
                color = ElectricViolet.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.3f))
            ) {
                Text(
                    text = current.pill,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = ElectricVioletLight,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = current.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                ),
                color = TextWhite,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = current.subtitle,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                ),
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Step dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                steps.indices.forEach { i ->
                    val isSelected = i == stepIndex
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(if (isSelected) 24.dp else 6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isSelected) ElectricViolet else DarkSurfaceTertiary)
                    )
                }
            }
        }

        // BOTTOM CTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            if (stepIndex < steps.size - 1) {
                Button(
                    onClick = { stepIndex++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
                }
            } else {
                RizzButton(
                    text = "Get Started ⚡",
                    onClick = { viewModel.completeOnboarding() }
                )
            }
        }
    }
}
