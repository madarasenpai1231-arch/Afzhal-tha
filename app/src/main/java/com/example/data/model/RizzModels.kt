package com.example.data.model

enum class Vibe(val label: String, val emoji: String, val tag: String, val description: String) {
    RIZZ("Rizz", "🔥", "Flirty & Magnetic", "Effortless charisma and confident charm"),
    FUNNY("Funny", "😂", "Witty & Playful", "Clever punchlines that break the ice"),
    SMOOTH("Smooth", "😎", "Calm & Collected", "Effortless, low pressure, and suave"),
    COLD("Cold", "🥶", "Detached & Unbothered", "High status, subtle intrigue, minimalist"),
    ROMANTIC("Romantic", "❤️", "Sweet & Genuine", "Warm, heartfelt, and memorable"),
    CUTE("Cute", "🥺", "Wholesome & Soft", "Endearing charm without being cringe"),
    CLEVER("Clever", "🧠", "Sharp & Intellectual", "Observational humor and subtle wit"),
    SAVAGE("Savage", "😈", "Cheeky & Bold", "Playful roasts and teasing banter"),
    PROFESSIONAL("Professional", "💼", "Polished & Crisp", "Respectful, crisp, and high-standard")
}

enum class RizzIntensity(val level: Int, val title: String, val microcopy: String) {
    LEVEL_1(1, "Friendly", "Keep it chill."),
    LEVEL_2(2, "Playful", "Light teasing & banter."),
    LEVEL_3(3, "Flirty", "Okay, we're cooking."),
    LEVEL_4(4, "Bold", "Taking the initiative."),
    LEVEL_5(5, "Dangerous 😈", "Proceed with confidence.")
}

enum class ToneModifier(val label: String, val emoji: String) {
    FLIRTIER("Flirtier", "👀"),
    FUNNIER("Funnier", "😂"),
    SHORTER("Shorter", "⚡"),
    MORE_CONFIDENT("More confident", "👑"),
    LESS_CRINGE("Less Cringe", "✨"),
    MORE_NATURAL("More natural", "🍃"),
    MORE_GEN_Z("More Gen-Z", "💀"),
    MORE_RIZZ("More Rizz", "🔥")
}

data class ScoreBreakdown(
    val overall: Int,
    val smoothness: Int,
    val creativity: Int,
    val confidence: Int,
    val naturalness: Int,
    val humor: Int
)

data class RizzReply(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val styleLabel: String,
    val emoji: String,
    val isBestPick: Boolean = false,
    val tag: String = "Natural + playful",
    val score: ScoreBreakdown = ScoreBreakdown(88, 91, 84, 88, 93, 76)
)

data class ConversationAnalysis(
    val energyScore: Int,
    val energyLabel: String,
    val playfulnessScore: Int,
    val questionBalanceScore: Int,
    val momentumScore: Int,
    val awkwardnessScore: Int,
    val whatsWorking: String,
    val potentialIssue: String,
    val tryThis: String,
    val recommendedNextMessage: String
)

data class MeaningOption(
    val label: String,
    val indicatorColor: String, // GREEN, YELLOW, ORANGE
    val percentage: Int,
    val interpretation: String
)

data class MeaningAnalysis(
    val originalText: String,
    val meanings: List<MeaningOption>,
    val bestResponses: List<String>
)

data class PreSendAudit(
    val originalDraft: String,
    val confidenceScore: Int,
    val naturalnessScore: Int,
    val cringeRiskScore: Int,
    val flirtLevelScore: Int,
    val pressureLevelScore: Int,
    val verdict: String,
    val fixedVersion: String
)

data class StarterCategory(
    val id: String,
    val label: String,
    val emoji: String,
    val description: String
)

data class RescueSituation(
    val id: String,
    val label: String,
    val emoji: String,
    val subhead: String,
    val playfulStrategy: String,
    val directStrategy: String,
    val lowPressureStrategy: String
)

data class RizzBattleScenario(
    val id: String,
    val incomingMessage: String,
    val optionA: String,
    val optionB: String,
    val initialVotesA: Int,
    val initialVotesB: Int,
    val pickFeedbackA: String,
    val pickFeedbackB: String
)

data class WingmanPersona(
    val id: String,
    val name: String,
    val tag: String,
    val quote: String,
    val emoji: String
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: String,
    val priceValue: Int,
    val subtitle: String,
    val benefits: List<String>,
    val isPopular: Boolean = false,
    val isCurrent: Boolean = false,
    val dailyLimit: Int = 5,
    val screenshotLimit: Int = 2
)

enum class SubscriptionStatus {
    NONE,
    ACTIVE,
    TRIALING,
    PAST_DUE,
    CANCELED,
    EXPIRED,
    PENDING
}

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()
    data class Success(val message: String = "Subscription activated! Enjoy your upgraded RizzX experience.") : PaymentState()
    data class Pending(val message: String = "Your payment is being confirmed. We'll update your RizzX access automatically.") : PaymentState()
    data class Failed(val message: String = "Your payment didn't go through. No worries — you haven't been charged by RizzX unless your payment provider confirms the transaction.") : PaymentState()
    data class Restored(val message: String = "Your subscription has been restored.") : PaymentState()
    data class AlreadySubscribed(val message: String = "You're already subscribed to this plan.") : PaymentState()
    data class NetworkError(val message: String = "We couldn't connect right now. Check your connection and try again.") : PaymentState()
}

enum class UserStyle(val label: String, val emoji: String, val description: String) {
    SMOOTH("Smooth", "😎", "Effortless, confident, and low pressure"),
    FUNNY("Funny", "😂", "Playful wit and unexpected observational punchlines"),
    FLIRTY("Flirty", "🔥", "Magnetic charm and clear attraction"),
    CALM("Calm", "🍃", "Grounded, unbothered, and authentic"),
    BOLD("Bold", "👑", "Direct, assertive, and high status"),
    CUTE("Cute", "🫶", "Warm, wholesome, and endearing"),
    CLEVER("Clever", "🧠", "Sharp, intellectually intriguing, and banter-ready")
}

data class ScreenshotMetrics(
    val confidence: String = "High",
    val naturalness: String = "High",
    val flirtLevel: String = "Medium",
    val cringeRisk: String = "Low",
    val momentum: String = "Strong"
)

data class ScreenshotAnalysisResult(
    val conversationSummary: String,
    val latestMessage: String,
    val tone: String,
    val vibeRead: String,
    val suggestedStrategy: String,
    val otherPersonStatus: String, // "They may be interested but keeping it casual"
    val responses: List<RizzReply>,
    val rizzScore: Int = 87,
    val metrics: ScreenshotMetrics = ScreenshotMetrics(),
    val imageUriString: String? = null
)

data class UsageLimitInfo(
    val used: Int,
    val limit: Int,
    val remaining: Int,
    val isLimitReached: Boolean
)
