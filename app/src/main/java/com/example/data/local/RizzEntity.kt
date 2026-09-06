package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rizz_history")
data class RizzHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val inputText: String,
    val replyText: String,
    val vibe: String,
    val score: Int = 88,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "reply" // "reply", "starter", "rescue", "analyzer"
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val rizzLevel: Int = 12,
    val rankTitle: String = "Smooth Talker",
    val rizzXp: Int = 340,
    val streakDays: Int = 4,
    val dailyUsageCount: Int = 2,
    val dailyLimit: Int = 5,
    val screenshotUsageToday: Int = 0,
    val selectedPlan: String = "FREE",
    val subscriptionStatus: String = "ACTIVE",
    val userStyle: String = "Smooth",
    val hasCompletedOnboarding: Boolean = false,
    val wingmanPersonaId: String = "smooth",
    val saveHistory: Boolean = true,
    val saveFavorites: Boolean = true,
    val dailyChallengeDone: Boolean = false,
    val preferredLanguage: String = "English",
    val preferredEmojiLevel: String = "Low",
    val preferredLength: String = "Short",
    val relationshipContext: String = "Crush",
    val lastUsageResetDate: Long = System.currentTimeMillis()
)
