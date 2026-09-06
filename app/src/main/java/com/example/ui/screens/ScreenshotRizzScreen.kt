package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RizzReply
import com.example.data.model.ToneModifier
import com.example.ui.components.RizzButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.RizzViewModel

@Composable
fun ScreenshotRizzScreen(
    viewModel: RizzViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenshotUri by viewModel.screenshotUri.collectAsState()
    val screenshotBitmap by viewModel.screenshotBitmap.collectAsState()
    val isScanning by viewModel.isScreenshotScanning.collectAsState()
    val scanningStep by viewModel.screenshotScanningStep.collectAsState()
    val screenshotResult by viewModel.screenshotResult.collectAsState()
    val showContextReview by viewModel.showContextReview.collectAsState()
    val selectedReplyForRefine by viewModel.selectedReplyForRefine.collectAsState()
    val isRefining by viewModel.isRefining.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Zero-permission modern Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.onScreenshotSelected(it, context) }
    }

    // Generic file / image picker fallback
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onScreenshotSelected(it, context) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBgPrimary)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // TOP APP BAR
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppDestination.HOME) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceTertiary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Screenshot Rizz",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.3).sp
                                ),
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "👀", fontSize = 18.sp)
                        }
                        Text(
                            text = "Upload the chat. We'll handle the reply.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { showPrivacyDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceTertiary)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = "Privacy Notice",
                        tint = ElectricVioletLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // STEP 1: UPLOAD STATE (WHEN NO IMAGE IS SELECTED)
        if (screenshotBitmap == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .testTag("upload_screenshot_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                    border = BorderStroke(1.5.dp, ElectricViolet.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📸", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Upload Conversation Screenshot",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Works with Instagram, Tinder, Hinge, WhatsApp, Bumble, iMessage & more.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("select_from_gallery_button")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    filePickerLauncher.launch("image/*")
                                },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0x44FFFFFF))
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextWhite)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Choose File", color = TextWhite)
                            }
                        }
                    }
                }
            }

            // PRIVACY BANNER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceSecondary),
                    border = BorderStroke(1.dp, Color(0x2210B981))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔒", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Your screenshots are private",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )
                            Text(
                                text = "RizzX processes your screenshot temporarily to draft replies. No screenshots are saved permanently or used for training.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // STEP 2: PREVIEW STATE (WHEN IMAGE IS LOADED BUT NOT SCANNED)
        if (screenshotBitmap != null && !showContextReview) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                    border = BorderStroke(1.5.dp, ElectricViolet.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Screenshot",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextWhite
                            )

                            TextButton(
                                onClick = { viewModel.clearScreenshot() }
                            ) {
                                Text("Remove", color = DestructiveRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // IMAGE PREVIEW
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 220.dp, max = 340.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(DarkBgPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = screenshotBitmap!!.asImageBitmap(),
                                contentDescription = "Screenshot Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp)),
                                contentScale = ContentScale.Fit
                            )

                            // SCANNING OVERLAY
                            if (isScanning) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xCC08080B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = ElectricViolet,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = scanningStep,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextWhite
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Extracting messages & chemistry…",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // ACTION BUTTONS
                        if (!isScanning) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0x33FFFFFF))
                                ) {
                                    Text("Change Image", color = TextWhite)
                                }

                                RizzButton(
                                    text = "Generate Rizz 🔥",
                                    onClick = { viewModel.processScreenshot(context) },
                                    modifier = Modifier.weight(1.3f).testTag("generate_rizz_from_screenshot_button")
                                )
                            }
                        }
                    }
                }
            }
        }

        // STEP 3: CONTEXT REVIEW & RESULTS STATE ("I got the context 👀")
        if (showContextReview && screenshotResult != null) {
            val result = screenshotResult!!

            // SECTION 7: DEDICATED CONTEXT CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCardElevated),
                    border = BorderStroke(1.5.dp, ElectricViolet.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "I got the context",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "👀", fontSize = 20.sp)
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = ElectricViolet.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚡", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rizz Score: ${result.rizzScore}/100",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElectricVioletLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // LATEST MESSAGE DETECTED
                        Text(
                            text = "THEIR LATEST MESSAGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurfaceTertiary
                        ) {
                            Text(
                                text = "“${result.latestMessage}”",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // VIBE & STRATEGY
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = DarkSurfaceSecondary
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "CONVERSATION VIBE",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = result.tone,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = WarmPinkLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = DarkSurfaceSecondary
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "MOMENTUM",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = result.metrics.momentum,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SUGGESTED APPROACH
                        Text(
                            text = "SUGGESTED APPROACH",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = result.suggestedStrategy,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // OTHER PERSON READING
                        Text(
                            text = result.otherPersonStatus,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.clearScreenshot()
                                }
                            ) {
                                Text("New Screenshot", color = TextSecondary)
                            }

                            Button(
                                onClick = { viewModel.navigateTo(AppDestination.RESULTS) },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Open Full Studio →", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // REPLIES HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5 Custom Replies Ready 🔥",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                    Text(
                        text = "Tap to copy",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // 5 GENERATED REPLIES CARDS
            items(result.responses) { reply ->
                var isCopied by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            viewModel.copyToClipboard(reply.text)
                            isCopied = true
                        }
                        .testTag("screenshot_reply_card_${reply.styleLabel}"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (reply.isBestPick) DarkCardElevated else DarkSurfaceSecondary
                    ),
                    border = BorderStroke(
                        width = if (reply.isBestPick) 1.5.dp else 1.dp,
                        color = if (reply.isBestPick) ElectricViolet else Color(0x22FFFFFF)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = reply.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = reply.styleLabel.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (reply.isBestPick) ElectricVioletLight else WarmPinkLight
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${reply.tag}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (isCopied) SuccessGreen.copy(alpha = 0.2f) else ElectricViolet.copy(alpha = 0.15f),
                                modifier = Modifier.clickable {
                                    viewModel.copyToClipboard(reply.text)
                                    isCopied = true
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isCopied) "✓ Copied" else "Copy",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isCopied) SuccessGreen else ElectricVioletLight,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = reply.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = TextWhite,
                                lineHeight = 22.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Refine & Share Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.selectReplyForRefine(reply)
                                    viewModel.navigateTo(AppDestination.RESULTS)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp), tint = ElectricVioletLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Refine", style = MaterialTheme.typography.labelSmall.copy(color = ElectricVioletLight))
                            }

                            TextButton(
                                onClick = {
                                    viewModel.saveToFavorites(reply)
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Outlined.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = WarmPinkLight)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", style = MaterialTheme.typography.labelSmall.copy(color = WarmPinkLight))
                            }
                        }
                    }
                }
            }
        }
    }

    // PRIVACY NOTICE DIALOG
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = DarkCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔒", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Screenshot Privacy",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextWhite
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• Your screenshots are private.\n• RizzX processes uploaded screenshots in-memory only to parse the conversation and generate your replies.\n• Screenshots are never stored on external databases or used to train public AI models.\n• Images are purged immediately after processing.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Text("Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
