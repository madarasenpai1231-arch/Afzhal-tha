package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.RizzHistoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.*
import com.example.data.repository.RizzEngine
import com.example.data.repository.RizzRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import com.example.data.remote.BillingManager

data class UiToast(
    val message: String,
    val icon: String = "✨"
)

enum class AppDestination {
    HOME,
    SCREENSHOT_RIZZ,
    RIZZ,
    RESULTS,
    ANALYZER,
    WHAT_THEY_MEAN,
    SHOULD_I_SEND,
    STARTERS,
    CPR_RESCUE,
    BATTLE,
    HISTORY,
    PROFILE,
    SUBSCRIPTION,
    ONBOARDING
}

class RizzViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RizzRepository
    private val vibrator: Vibrator?

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RizzRepository(db.rizzDao())
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        viewModelScope.launch {
            repository.ensureProfileInitialized()
        }
    }

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .map { it ?: UserProfileEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val historyList: StateFlow<List<RizzHistoryEntity>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<RizzHistoryEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentHistory: StateFlow<List<RizzHistoryEntity>> = repository.getRecentHistory(4)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Navigation
    private val _currentScreen = MutableStateFlow(AppDestination.HOME)
    val currentScreen: StateFlow<AppDestination> = _currentScreen.asStateFlow()

    // Home / Generator State
    var inputMessage = MutableStateFlow("")
        private set
    var selectedVibe = MutableStateFlow(Vibe.RIZZ)
        private set
    var selectedIntensity = MutableStateFlow(RizzIntensity.LEVEL_3)
        private set
    var relationshipContext = MutableStateFlow("Crush")
        private set
    var messageLength = MutableStateFlow("Short")
        private set
    var emojiLevel = MutableStateFlow("Low")
        private set
    var activeModifier = MutableStateFlow<ToneModifier?>(null)
        private set

    // Generation state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _loadingText = MutableStateFlow("Cooking up something good…")
    val loadingText: StateFlow<String> = _loadingText.asStateFlow()

    private val _generatedReplies = MutableStateFlow<List<RizzReply>>(emptyList())
    val generatedReplies: StateFlow<List<RizzReply>> = _generatedReplies.asStateFlow()

    // Transient Toast State
    private val _toast = MutableStateFlow<UiToast?>(null)
    val toast: StateFlow<UiToast?> = _toast.asStateFlow()

    // Analyzer State
    var analyzerInput = MutableStateFlow("")
        private set
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    private val _analysisResult = MutableStateFlow<ConversationAnalysis?>(null)
    val analysisResult: StateFlow<ConversationAnalysis?> = _analysisResult.asStateFlow()

    // What Do They Mean State
    var meanInput = MutableStateFlow("Okayyy sure 😂")
        private set
    private val _isDecoding = MutableStateFlow(false)
    val isDecoding: StateFlow<Boolean> = _isDecoding.asStateFlow()
    private val _meaningResult = MutableStateFlow<MeaningAnalysis?>(null)
    val meaningResult: StateFlow<MeaningAnalysis?> = _meaningResult.asStateFlow()

    // Should I Send This State
    var shouldISendInput = MutableStateFlow("")
        private set
    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()
    private val _auditResult = MutableStateFlow<PreSendAudit?>(null)
    val auditResult: StateFlow<PreSendAudit?> = _auditResult.asStateFlow()

    // Starters State
    var selectedStarterCategory = MutableStateFlow("crush")
        private set
    var currentStarters = MutableStateFlow(RizzEngine.getStartersForCategory("crush"))
        private set

    // Rescue State
    var selectedRescueSituation = MutableStateFlow(RizzEngine.rescueSituations.first())
        private set

    // Battle State
    var currentBattleIndex = MutableStateFlow(0)
        private set
    var userBattlePick = MutableStateFlow<String?>(null) // "A" or "B"
        private set

    // Screenshot Rizz State
    private val _screenshotUri = MutableStateFlow<Uri?>(null)
    val screenshotUri: StateFlow<Uri?> = _screenshotUri.asStateFlow()

    private val _screenshotBitmap = MutableStateFlow<Bitmap?>(null)
    val screenshotBitmap: StateFlow<Bitmap?> = _screenshotBitmap.asStateFlow()

    private val _isScreenshotScanning = MutableStateFlow(false)
    val isScreenshotScanning: StateFlow<Boolean> = _isScreenshotScanning.asStateFlow()

    private val _screenshotScanningStep = MutableStateFlow("Reading screenshot…")
    val screenshotScanningStep: StateFlow<String> = _screenshotScanningStep.asStateFlow()

    private val _screenshotResult = MutableStateFlow<ScreenshotAnalysisResult?>(null)
    val screenshotResult: StateFlow<ScreenshotAnalysisResult?> = _screenshotResult.asStateFlow()

    private val _showContextReview = MutableStateFlow(false)
    val showContextReview: StateFlow<Boolean> = _showContextReview.asStateFlow()

    // Response micro-refinement
    private val _selectedReplyForRefine = MutableStateFlow<RizzReply?>(null)
    val selectedReplyForRefine: StateFlow<RizzReply?> = _selectedReplyForRefine.asStateFlow()

    private val _isRefining = MutableStateFlow(false)
    val isRefining: StateFlow<Boolean> = _isRefining.asStateFlow()

    // Usage limit dialog
    private val _showLimitReachedDialog = MutableStateFlow(false)
    val showLimitReachedDialog: StateFlow<Boolean> = _showLimitReachedDialog.asStateFlow()

    // Payment state
    val paymentState: StateFlow<PaymentState> = BillingManager.paymentState

    // History Search
    var historySearchQuery = MutableStateFlow("")
        private set

    private var loadingJob: Job? = null

    fun navigateTo(dest: AppDestination) {
        _currentScreen.value = dest
    }

    fun setInput(text: String) {
        inputMessage.value = text
    }

    fun setVibe(vibe: Vibe) {
        selectedVibe.value = vibe
        triggerLightHaptic()
    }

    fun setIntensity(level: RizzIntensity) {
        selectedIntensity.value = level
        triggerLightHaptic()
    }

    fun setRelationship(context: String) {
        relationshipContext.value = context
    }

    fun setMessageLength(len: String) {
        messageLength.value = len
    }

    fun setEmojiLevel(level: String) {
        emojiLevel.value = level
    }

    fun generateRizz(modifierOverride: ToneModifier? = null) {
        val currentProfile = userProfile.value
        if (currentProfile.dailyUsageCount >= currentProfile.dailyLimit) {
            _showLimitReachedDialog.value = true
            return
        }

        val text = inputMessage.value.trim()
        if (text.isEmpty()) {
            showToast("Paste or type a message first!", "✍️")
            return
        }

        triggerMediumHaptic()
        _isGenerating.value = true
        activeModifier.value = modifierOverride

        // Rotate loading texts
        loadingJob?.cancel()
        val loadingTexts = listOf(
            "Reading the vibe…",
            "Cooking up something good…",
            "Finding the smoothest angle…",
            "Removing the awkwardness…",
            "Loading the rizz…"
        )
        loadingJob = viewModelScope.launch {
            var i = 0
            while (_isGenerating.value) {
                _loadingText.value = loadingTexts[i % loadingTexts.size]
                i++
                delay(600)
            }
        }

        viewModelScope.launch {
            try {
                // Minimum duration for anticipation feedback
                val delayTime = if (modifierOverride != null) 300L else 500L
                val startTime = System.currentTimeMillis()

                val result = repository.generateReplies(
                    incoming = text,
                    vibe = selectedVibe.value,
                    intensity = selectedIntensity.value,
                    relationshipContext = relationshipContext.value,
                    length = messageLength.value,
                    emojiLevel = emojiLevel.value,
                    modifier = modifierOverride
                )

                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < delayTime) {
                    delay(delayTime - elapsed)
                }

                _generatedReplies.value = result
                _currentScreen.value = AppDestination.RESULTS
                triggerLightHaptic()
            } catch (e: Exception) {
                showToast("Something went sideways. Try again.", "⚠️")
            } finally {
                _isGenerating.value = false
                loadingJob?.cancel()
            }
        }
    }

    fun refineReplies(modifier: ToneModifier) {
        generateRizz(modifierOverride = modifier)
        val notice = when (modifier) {
            ToneModifier.LESS_CRINGE -> "Okay. We brought it back to earth."
            ToneModifier.MORE_RIZZ -> "Turned up the heat 🔥"
            ToneModifier.SHORTER -> "Trimmed to the point ⚡"
            else -> "Remixing with ${modifier.label} vibe"
        }
        showToast(notice, modifier.emoji)
    }

    fun copyToClipboard(text: String, customNotice: String = "Copied. Go cause some trouble 😌") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("RizzX Reply", text)
        clipboard.setPrimaryClip(clip)
        triggerLightHaptic()
        showToast(customNotice, "✓")
    }

    fun saveToFavorites(reply: RizzReply) {
        viewModelScope.launch {
            repository.saveReplyToVault(
                incoming = inputMessage.value.ifBlank { "Conversation line" },
                reply = reply,
                vibe = selectedVibe.value.label
            )
            triggerLightHaptic()
            showToast("Added to the Hall of Fame 🏆", "🏆")
        }
    }

    fun toggleFavoriteItem(item: RizzHistoryEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, !item.isFavorite)
            triggerLightHaptic()
            if (!item.isFavorite) {
                showToast("Added to the Hall of Fame 🏆", "⭐")
            } else {
                showToast("Removed from favorites", "🗑️")
            }
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistory(id)
            showToast("Item deleted", "🗑️")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            showToast("All history cleared", "🧹")
        }
    }

    fun completeDailyChallenge() {
        viewModelScope.launch {
            repository.completeChallenge()
            triggerMediumHaptic()
            showToast("Daily Challenge Complete! +25 XP 🔥", "🎉")
        }
    }

    fun selectStarterCategory(catId: String) {
        selectedStarterCategory.value = catId
        currentStarters.value = RizzEngine.getStartersForCategory(catId)
        triggerLightHaptic()
    }

    fun selectRescueSituation(situation: RescueSituation) {
        selectedRescueSituation.value = situation
        triggerLightHaptic()
    }

    fun analyzeConversation() {
        val text = analyzerInput.value.trim()
        if (text.isEmpty()) {
            showToast("Drop a conversation transcript first!", "📝")
            return
        }
        _isAnalyzing.value = true
        viewModelScope.launch {
            try {
                val res = RizzEngine.analyzeConversation(text)
                _analysisResult.value = res
                triggerLightHaptic()
            } catch (e: Exception) {
                showToast("Analysis interrupted", "⚠️")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun analyzeWhatDoTheyMean() {
        val text = meanInput.value.trim()
        if (text.isEmpty()) return
        _isDecoding.value = true
        viewModelScope.launch {
            try {
                val res = RizzEngine.analyzeWhatDoTheyMean(text)
                _meaningResult.value = res
                triggerLightHaptic()
            } catch (e: Exception) {
                showToast("Could not decode message", "⚠️")
            } finally {
                _isDecoding.value = false
            }
        }
    }

    fun runPreSendAudit() {
        val text = shouldISendInput.value.trim()
        if (text.isEmpty()) {
            showToast("Enter your draft message first!", "✍️")
            return
        }
        _isAuditing.value = true
        viewModelScope.launch {
            try {
                val res = RizzEngine.checkShouldISendThis(text)
                _auditResult.value = res
                triggerLightHaptic()
            } catch (e: Exception) {
                showToast("Could not check message", "⚠️")
            } finally {
                _isAuditing.value = false
            }
        }
    }

    fun voteBattle(option: String) {
        userBattlePick.value = option
        triggerLightHaptic()
    }

    fun onScreenshotSelected(uri: Uri, context: Context) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap != null) {
                    // Downscale to max 1080 to conserve memory and optimize vision analysis
                    val maxDimension = 1080
                    val width = originalBitmap.width
                    val height = originalBitmap.height
                    val scaledBitmap = if (width > maxDimension || height > maxDimension) {
                        val ratio = width.toFloat() / height.toFloat()
                        val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
                        val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
                        Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                    } else {
                        originalBitmap
                    }

                    _screenshotUri.value = uri
                    _screenshotBitmap.value = scaledBitmap
                    _screenshotResult.value = null
                    _showContextReview.value = false
                    _currentScreen.value = AppDestination.SCREENSHOT_RIZZ
                    triggerLightHaptic()
                } else {
                    showToast("Could not open this image format. Try JPG or PNG.", "⚠️")
                }
            } catch (e: Exception) {
                showToast("Failed to load screenshot", "⚠️")
            }
        }
    }

    fun clearScreenshot() {
        _screenshotUri.value = null
        _screenshotBitmap.value = null
        _screenshotResult.value = null
        _showContextReview.value = false
    }

    fun processScreenshot(context: Context) {
        val currentProfile = userProfile.value
        if (currentProfile.dailyUsageCount >= currentProfile.dailyLimit) {
            _showLimitReachedDialog.value = true
            return
        }

        val bitmap = _screenshotBitmap.value
        val uri = _screenshotUri.value
        if (bitmap == null) {
            showToast("Upload or pick a screenshot first!", "📸")
            return
        }

        _isScreenshotScanning.value = true
        triggerMediumHaptic()

        val scanSteps = listOf(
            "Reading screenshot…",
            "Understanding context…",
            "Finding the best angle…",
            "Writing your Rizz…"
        )

        val scanJob = viewModelScope.launch {
            var stepIndex = 0
            while (_isScreenshotScanning.value) {
                _screenshotScanningStep.value = scanSteps[stepIndex % scanSteps.size]
                stepIndex++
                delay(600)
            }
        }

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Compress bitmap to base64 JPEG
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 82, outputStream)
                val byteArray = outputStream.toByteArray()
                val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)

                val analysis = repository.analyzeScreenshot(
                    base64Image = base64,
                    imageUriString = uri?.toString()
                )

                _screenshotResult.value = analysis
                _generatedReplies.value = analysis.responses
                _showContextReview.value = true
                triggerLightHaptic()
            } catch (e: Exception) {
                showToast("Analysis interrupted. Try again.", "⚠️")
            } finally {
                _isScreenshotScanning.value = false
                scanJob.cancel()
            }
        }
    }

    fun selectReplyForRefine(reply: RizzReply?) {
        _selectedReplyForRefine.value = reply
        triggerLightHaptic()
    }

    fun refineSpecificReply(targetReply: RizzReply, modifier: ToneModifier) {
        _isRefining.value = true
        triggerLightHaptic()
        viewModelScope.launch {
            try {
                val newText = RizzEngine.refineReply(targetReply.text, modifier)
                val updatedReply = targetReply.copy(
                    text = newText,
                    tag = "${targetReply.styleLabel} • ${modifier.label}"
                )

                // Update in generated replies
                _generatedReplies.value = _generatedReplies.value.map {
                    if (it.id == targetReply.id) updatedReply else it
                }

                // Update in screenshot result if active
                _screenshotResult.value?.let { current ->
                    _screenshotResult.value = current.copy(
                        responses = current.responses.map { if (it.id == targetReply.id) updatedReply else it }
                    )
                }

                _selectedReplyForRefine.value = updatedReply
                val notice = when (modifier) {
                    ToneModifier.LESS_CRINGE -> "Okay. We brought it back to earth."
                    ToneModifier.MORE_RIZZ -> "Turned up the heat 🔥"
                    ToneModifier.SHORTER -> "Trimmed to the point ⚡"
                    else -> "Remixed with ${modifier.label} vibe"
                }
                showToast(notice, modifier.emoji)
            } catch (e: Exception) {
                showToast("Could not refine reply", "⚠️")
            } finally {
                _isRefining.value = false
            }
        }
    }

    fun setUserStyle(style: UserStyle) {
        viewModelScope.launch {
            repository.setUserStyle(style.label)
            showToast("Style set to ${style.label} ${style.emoji}", "✨")
        }
    }

    fun purchasePlan(planId: String) {
        viewModelScope.launch {
            val success = BillingManager.purchasePlan(planId, userProfile.value.selectedPlan)
            if (success) {
                repository.setPlan(planId)
            }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val (restored, planId) = BillingManager.restorePurchases(userProfile.value.selectedPlan)
            if (restored && planId != null) {
                repository.setPlan(planId)
            }
        }
    }

    fun dismissLimitDialog() {
        _showLimitReachedDialog.value = false
    }

    fun resetPaymentState() {
        BillingManager.resetPaymentState()
    }

    fun nextBattle() {
        userBattlePick.value = null
        currentBattleIndex.value = (currentBattleIndex.value + 1) % RizzEngine.battleScenarios.size
        triggerLightHaptic()
    }

    fun selectPlan(planId: String) {
        viewModelScope.launch {
            repository.setPlan(planId)
            showToast("Plan updated to $planId 👑", "✨")
        }
    }

    fun selectWingman(wingmanId: String) {
        viewModelScope.launch {
            repository.setWingman(wingmanId)
            showToast("AI Wingman updated!", "😎")
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted()
            _currentScreen.value = AppDestination.HOME
        }
    }

    fun showToast(msg: String, icon: String = "✨") {
        _toast.value = UiToast(msg, icon)
        viewModelScope.launch {
            delay(2400)
            if (_toast.value?.message == msg) {
                _toast.value = null
            }
        }
    }

    private fun triggerLightHaptic() {
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(20)
                }
            }
        } catch (_: Exception) {}
    }

    private fun triggerMediumHaptic() {
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(45)
                }
            }
        } catch (_: Exception) {}
    }
}
