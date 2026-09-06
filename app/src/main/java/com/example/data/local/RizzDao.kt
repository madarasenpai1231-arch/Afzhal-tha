package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RizzDao {
    @Query("SELECT * FROM rizz_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<RizzHistoryEntity>>

    @Query("SELECT * FROM rizz_history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<RizzHistoryEntity>>

    @Query("SELECT * FROM rizz_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<RizzHistoryEntity>>

    @Query("SELECT * FROM rizz_history WHERE inputText LIKE '%' || :query || '%' OR replyText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchHistory(query: String): Flow<List<RizzHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: RizzHistoryEntity): Long

    @Query("UPDATE rizz_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM rizz_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM rizz_history")
    suspend fun clearAllHistory()

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET dailyUsageCount = dailyUsageCount + 1 WHERE id = 1")
    suspend fun incrementDailyUsage()

    @Query("UPDATE user_profile SET dailyUsageCount = dailyUsageCount + 1, screenshotUsageToday = screenshotUsageToday + 1 WHERE id = 1")
    suspend fun incrementScreenshotUsage()

    @Query("UPDATE user_profile SET dailyUsageCount = 0, screenshotUsageToday = 0, lastUsageResetDate = :timestamp WHERE id = 1")
    suspend fun resetDailyUsage(timestamp: Long)

    @Query("UPDATE user_profile SET rizzXp = rizzXp + :xpAmount WHERE id = 1")
    suspend fun addXp(xpAmount: Int)

    @Query("UPDATE user_profile SET dailyChallengeDone = 1, streakDays = streakDays + 1 WHERE id = 1")
    suspend fun completeDailyChallenge()
}
