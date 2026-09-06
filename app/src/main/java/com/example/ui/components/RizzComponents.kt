package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RizzIntensity
import com.example.data.model.RizzReply
import com.example.data.model.Vibe
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RizzButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    loadingText: String = "Cooking up something good…",
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(),
        label = "button_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = if (enabled) RizzGradient else Brush.horizontalGradient(
                    listOf(Color(0xFF374151), Color(0xFF4B5563))
                )
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                AnimatedContent(
                    targetState = loadingText,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "loading_text"
                ) { target ->
                    Text(
                        text = target,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = SparkleYellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    border: BorderStroke? = BorderStroke(1.dp, Color(0x228B5CF6)),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
        border = border
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun ModePill(
    vibe: Vibe,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgModifier = if (isSelected) {
        Modifier.background(RizzGradient)
    } else {
        Modifier.background(DarkSurfaceTertiary)
    }

    val borderStroke = if (isSelected) {
        BorderStroke(1.dp, Color(0x66FFFFFF))
    } else {
        BorderStroke(1.dp, Color(0x1AFFFFFF))
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(999.dp),
        color = Color.Transparent,
        border = borderStroke
    ) {
        Box(
            modifier = Modifier
                .then(bgModifier)
                .padding(horizontal = 16.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = vibe.emoji, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = vibe.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RizzIntensitySlider(
    current: RizzIntensity,
    onLevelSelected: (RizzIntensity) -> Unit
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rizz Level",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricViolet.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${current.level} — ${current.title}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElectricVioletLight,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Text(
                text = "“${current.microcopy}”",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RizzIntensity.values().forEach { intensity ->
                val isSelected = intensity == current
                val isPastOrCurrent = intensity.level <= current.level

                val itemBgModifier = if (isSelected) {
                    Modifier.background(RizzGradient)
                } else if (isPastOrCurrent) {
                    Modifier.background(ElectricViolet.copy(alpha = 0.25f))
                } else {
                    Modifier.background(DarkSurfaceTertiary)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .then(itemBgModifier)
                        .clickable { onLevelSelected(intensity) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${intensity.level}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ResultCard(
    reply: RizzReply,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onRefineBolder: (() -> Unit)? = null
) {
    var isCopied by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    val border = if (reply.isBestPick) {
        BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(ElectricViolet, WarmPink)))
    } else {
        BorderStroke(1.dp, Color(0x22FFFFFF))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reply.isBestPick) DarkSurfaceSecondary else DarkCardElevated
        ),
        border = border
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = reply.emoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reply.styleLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (reply.isBestPick) ElectricVioletLight else TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0x22FFFFFF)
                ) {
                    Text(
                        text = reply.tag,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body text
            Text(
                text = reply.text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary Copy Button
                Button(
                    onClick = {
                        isCopied = true
                        onCopy()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCopied) SuccessGreen else ElectricViolet
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Outlined.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCopied) "Copied ✓" else "Copy",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                LaunchedEffect(isCopied) {
                    if (isCopied) {
                        delay(1200)
                        isCopied = false
                    }
                }

                // Optional "Make it bolder" for Best Pick
                if (reply.isBestPick && onRefineBolder != null) {
                    TextButton(
                        onClick = onRefineBolder,
                        colors = ButtonDefaults.textButtonColors(contentColor = WarmPinkLight)
                    ) {
                        Text(
                            text = "Make it bolder 🔥",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                // Icon buttons: Save & Share
                Row {
                    IconButton(
                        onClick = {
                            isSaved = !isSaved
                            onSave()
                        }
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) SparkleYellow else TextSecondary
                        )
                    }

                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RizzScoreCard(reply: RizzReply) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Rizz Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite
                )
                Text(
                    text = "AI-generated entertainment estimate",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(RizzGradient),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${reply.score.overall}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Breakdown bars
        ScoreDimensionRow("Smoothness", reply.score.smoothness, ElectricVioletLight)
        ScoreDimensionRow("Creativity", reply.score.creativity, WarmPinkLight)
        ScoreDimensionRow("Confidence", reply.score.confidence, SparkleYellow)
        ScoreDimensionRow("Naturalness", reply.score.naturalness, SuccessGreen)
    }
}

@Composable
private fun ScoreDimensionRow(label: String, value: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.width(90.dp)
        )
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = color,
            trackColor = DarkSurfaceTertiary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$value",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = TextWhite,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun UsageIndicatorBar(used: Int, total: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = DarkSurfaceTertiary,
        border = BorderStroke(1.dp, Color(0x228B5CF6))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = SparkleYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$used / $total Rizzes",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            )
        }
    }
}

@Composable
fun ShareCardDialog(
    reply: RizzReply,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBgPrimary),
            border = BorderStroke(1.5.dp, RizzGradient)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Branded header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RizzX",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡",
                        fontSize = 20.sp
                    )
                }

                Text(
                    text = "Your conversation cheat code",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card content
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkCardElevated,
                    border = BorderStroke(1.dp, Color(0x22FFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = reply.text,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            ),
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rizz Score: ${reply.score.overall}/100",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SparkleYellow
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Never run out of things to say.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Text("Close", color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "“${reply.text}”\n\n— Generated with RizzX: Never run out of things to say."
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Rizz"))
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send it 😈")
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingToast(toast: String, icon: String = "✨") {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = DarkCardElevated,
        border = BorderStroke(1.dp, Color(0x338B5CF6)),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = toast,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextWhite
            )
        }
    }
}
