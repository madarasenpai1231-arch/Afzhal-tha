package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NavigateNext
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

@Composable
fun BattleScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val battleIndex by viewModel.currentBattleIndex.collectAsState()
    val userPick by viewModel.userBattlePick.collectAsState()
    val scenario = RizzEngine.battleScenarios[battleIndex % RizzEngine.battleScenarios.size]

    val totalVotes = scenario.initialVotesA + scenario.initialVotesB
    val percentA = (scenario.initialVotesA * 100) / totalVotes
    val percentB = 100 - percentA

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rizz Battle",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "⚔️", fontSize = 20.sp)
                        }
                        Text(
                            text = "Vote on the smoother response",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = DarkSurfaceTertiary
                ) {
                    Text(
                        text = "Round ${battleIndex + 1}/${RizzEngine.battleScenarios.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricVioletLight
                    )
                }
            }
        }

        // SCENARIO CARD
        item {
            GlassCard(border = BorderStroke(1.dp, Color(0x338B5CF6))) {
                Text(
                    text = "THE INCOMING MESSAGE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ElectricVioletLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = scenario.incomingMessage,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextWhite
                )
            }
        }

        // OPTION A
        item {
            OptionVoteCard(
                letter = "A",
                quote = scenario.optionA,
                percentage = percentA,
                isChosen = userPick == "A",
                hasVoted = userPick != null,
                feedback = scenario.pickFeedbackA,
                onVote = { viewModel.voteBattle("A") }
            )
        }

        // VS DIVIDER
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ),
                    color = WarmPinkLight
                )
            }
        }

        // OPTION B
        item {
            OptionVoteCard(
                letter = "B",
                quote = scenario.optionB,
                percentage = percentB,
                isChosen = userPick == "B",
                hasVoted = userPick != null,
                feedback = scenario.pickFeedbackB,
                onVote = { viewModel.voteBattle("B") }
            )
        }

        // NEXT BUTTON
        if (userPick != null) {
            item {
                Button(
                    onClick = { viewModel.nextBattle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Next Battle Round")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun OptionVoteCard(
    letter: String,
    quote: String,
    percentage: Int,
    isChosen: Boolean,
    hasVoted: Boolean,
    feedback: String,
    onVote: () -> Unit
) {
    val borderColor = if (isChosen) ElectricVioletLight else Color(0x22FFFFFF)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !hasVoted, onClick = onVote),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChosen) DarkSurfaceSecondary else DarkCardElevated
        ),
        border = BorderStroke(if (isChosen) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isChosen) ElectricViolet else DarkSurfaceTertiary
                ) {
                    Text(
                        text = "Option $letter",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                if (hasVoted) {
                    Text(
                        text = "$percentage% picked",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SparkleYellow
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = quote,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp
                ),
                color = TextWhite
            )

            if (hasVoted) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { percentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp)),
                    color = if (percentage > 50) SuccessGreen else ElectricViolet,
                    trackColor = DarkSurfaceTertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
