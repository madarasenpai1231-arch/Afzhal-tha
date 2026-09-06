package com.example.data.repository

import com.example.data.local.RizzDao
import com.example.data.local.RizzHistoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class RizzRepository(private val dao: RizzDao) {

    val history: Flow<List<RizzHistoryEntity>> = dao.getAllHistory()
    val favorites: Flow<List<RizzHistoryEntity>> = dao.getFavorites()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    fun getRecentHistory(limit: Int = 5): Flow<List<RizzHistoryEntity>> =
        dao.getRecentHistory(limit)

    fun searchHistory(query: String): Flow<List<RizzHistoryEntity>> =
        dao.searchHistory(query)

    suspend fun ensureProfileInitialized() {
        val current = dao.getUserProfile().firstOrNull()
        if (current == null) {
            dao.insertOrUpdateProfile(UserProfileEntity())
        }
    }

    suspend fun generateReplies(
        incoming: String,
        vibe: Vibe,
        intensity: RizzIntensity,
        relationshipContext: String = "Crush",
        length: String = "Short",
        emojiLevel: String = "Low",
        modifier: ToneModifier? = null
    ): List<RizzReply> {
        val replies = RizzEngine.generateReplies(
            incomingMessage = incoming,
            vibe = vibe,
            intensity = intensity,
            relationshipContext = relationshipContext,
            length = length,
            emojiLevel = emojiLevel,
            modifier = modifier
        )

        // Increment usage count and award 2 XP
        dao.incrementDailyUsage()
        dao.addXp(2)

        // If history is enabled, record the best pick
        val profile = dao.getUserProfile().firstOrNull()
        if (profile?.saveHistory != false && replies.isNotEmpty()) {
            val best = replies.firstOrNull { it.isBestPick } ?: replies.first()
            dao.insertHistory(
                RizzHistoryEntity(
                    inputText = incoming,
                    replyText = best.text,
                    vibe = vibe.label,
                    score = best.score.overall,
                    category = "reply"
                )
            )
        }

        return replies
    }

    suspend fun saveReplyToVault(incoming: String, reply: RizzReply, vibe: String): Long {
        dao.addXp(5)
        return dao.insertHistory(
            RizzHistoryEntity(
                inputText = incoming,
                replyText = reply.text,
                vibe = vibe,
                score = reply.score.overall,
                isFavorite = true,
                category = "favorite"
            )
        )
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        dao.updateFavorite(id, isFavorite)
        if (isFavorite) {
            dao.addXp(3)
        }
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteHistoryItem(id)
    }

    suspend fun clearHistory() {
        dao.clearAllHistory()
    }

    suspend fun completeChallenge() {
        dao.completeDailyChallenge()
        dao.addXp(25)
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun setOnboardingCompleted() {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        dao.insertOrUpdateProfile(current.copy(hasCompletedOnboarding = true))
    }

    suspend fun analyzeScreenshot(
        base64Image: String?,
        imageUriString: String? = null
    ): ScreenshotAnalysisResult {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val result = RizzEngine.analyzeScreenshot(
            base64Image = base64Image,
            imageUriString = imageUriString,
            userStyle = profile.userStyle
        )

        dao.incrementScreenshotUsage()
        dao.addXp(5)

        if (profile.saveHistory && result.responses.isNotEmpty()) {
            val best = result.responses.firstOrNull { it.isBestPick } ?: result.responses.first()
            dao.insertHistory(
                RizzHistoryEntity(
                    inputText = "📸 [Screenshot] ${result.latestMessage}",
                    replyText = best.text,
                    vibe = result.tone,
                    score = result.rizzScore,
                    category = "screenshot"
                )
            )
        }

        return result
    }

    suspend fun setUserStyle(styleName: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        dao.insertOrUpdateProfile(current.copy(userStyle = styleName))
    }

    suspend fun setPlan(planId: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val plan = com.example.data.remote.BillingManager.getPlan(planId)
        dao.insertOrUpdateProfile(
            current.copy(
                selectedPlan = plan.id,
                dailyLimit = plan.dailyLimit,
                subscriptionStatus = if (plan.id == "FREE") "NONE" else "ACTIVE"
            )
        )
    }

    suspend fun setWingman(wingmanId: String) {
        val current = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        dao.insertOrUpdateProfile(current.copy(wingmanPersonaId = wingmanId))
    }

    suspend fun resetUsage() {
        dao.resetDailyUsage(System.currentTimeMillis())
    }
}
