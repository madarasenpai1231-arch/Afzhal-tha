package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    // Per gemini-api skill: standard Gemini model is gemini-3.5-flash
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()
    }

    fun isConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY" && !key.contains("PLACEHOLDER", ignoreCase = true)
    }

    suspend fun generatePrompt(prompt: String, systemInstructionText: String? = null): String? = withContext(Dispatchers.IO) {
        if (!isConfigured()) return@withContext null

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val url = "$BASE_URL?key=$key"

            val jsonBody = JSONObject()

            // System instruction if present
            if (!systemInstructionText.isNullOrBlank()) {
                val systemContent = JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstructionText)))
                }
                jsonBody.put("systemInstruction", systemContent)
            }

            // User prompt
            val partsArray = JSONArray().put(JSONObject().put("text", prompt))
            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
            jsonBody.put("contents", contentsArray)

            val config = JSONObject().apply {
                put("temperature", 0.85)
                put("topP", 0.95)
                put("topK", 40)
            }
            jsonBody.put("generationConfig", config)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API failed with code ${response.code}: ${response.message}")
                return@withContext null
            }

            val responseBody = response.body?.string() ?: return@withContext null
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val contentObj = firstCandidate.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return@withContext parts.getJSONObject(0).optString("text")
                }
            }
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content with Gemini: ${e.message}", e)
            null
        }
    }

    /**
     * Multimodal Screenshot Analysis via Gemini Vision API
     */
    suspend fun analyzeScreenshotWithVision(
        base64Image: String,
        mimeType: String = "image/jpeg",
        userStyle: String = "Smooth"
    ): ScreenshotAnalysisResult? = withContext(Dispatchers.IO) {
        if (!isConfigured()) return@withContext null

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val url = "$BASE_URL?key=$key"

            val prompt = """
                You are RizzX, the ultimate AI conversation wingman.
                Analyze this dating / text conversation screenshot.
                Extract the visible chat messages, identify who is speaking, find the latest incoming message, and evaluate the tone, humor, flirting level, and momentum.
                
                Apply user's personal style preference: $userStyle.
                Do NOT output generic cliches like 'Hey beautiful' or 'What's up?'.
                Keep replies natural, human, confident, witty, low-pressure, and conversational.
                Use probabilistic wording for readings (e.g., 'They may be interested but keeping it casual', 'Possible interpretation: ...').
                
                Output ONLY a valid JSON object strictly conforming to this schema without markdown fences:
                {
                  "conversationSummary": "Brief overview of what was discussed",
                  "latestMessage": "Exact text of the latest incoming message to reply to",
                  "tone": "e.g. Playful & teasing",
                  "vibeRead": "e.g. High banter, slight flirtation",
                  "suggestedStrategy": "e.g. Keep it punchy and match their energy. Don't over-explain.",
                  "otherPersonStatus": "Possible interpretation: They seem engaged and open to hanging out.",
                  "rizzScore": 88,
                  "metrics": {
                    "confidence": "High",
                    "naturalness": "High",
                    "flirtLevel": "Medium",
                    "cringeRisk": "Low",
                    "momentum": "Strong"
                  },
                  "responses": [
                    {
                      "styleLabel": "Best Pick",
                      "emoji": "🏆",
                      "isBestPick": true,
                      "tag": "Most magnetic & balanced",
                      "text": "Reply text here..."
                    },
                    {
                      "styleLabel": "Smooth",
                      "emoji": "😏",
                      "isBestPick": false,
                      "tag": "Calm, confident charm",
                      "text": "Reply text here..."
                    },
                    {
                      "styleLabel": "Funny",
                      "emoji": "😂",
                      "isBestPick": false,
                      "tag": "Witty observational punchline",
                      "text": "Reply text here..."
                    },
                    {
                      "styleLabel": "Cute",
                      "emoji": "🫶",
                      "isBestPick": false,
                      "tag": "Warm & endearing",
                      "text": "Reply text here..."
                    },
                    {
                      "styleLabel": "Bold",
                      "emoji": "🔥",
                      "isBestPick": false,
                      "tag": "Direct initiative",
                      "text": "Reply text here..."
                    }
                  ]
                }
            """.trimIndent()

            val jsonBody = JSONObject()

            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", prompt))
            
            // Image inline data
            val inlineDataObj = JSONObject().apply {
                put("mimeType", mimeType)
                put("data", base64Image)
            }
            partsArray.put(JSONObject().put("inlineData", inlineDataObj))

            val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
            jsonBody.put("contents", contentsArray)

            val config = JSONObject().apply {
                put("temperature", 0.75)
                put("responseMimeType", "application/json")
            }
            jsonBody.put("generationConfig", config)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Screenshot Vision API failed: ${response.code}")
                return@withContext null
            }

            val responseBody = response.body?.string() ?: return@withContext null
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return@withContext null
            if (candidates.length() == 0) return@withContext null
            
            val contentObj = candidates.getJSONObject(0).optJSONObject("content")
            val parts = contentObj?.optJSONArray("parts") ?: return@withContext null
            if (parts.length() == 0) return@withContext null

            val rawJson = parts.getJSONObject(0).optString("text")
            val cleanedJson = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = JSONObject(cleanedJson)
            val conversationSummary = parsed.optString("conversationSummary", "Conversation detected")
            val latestMessage = parsed.optString("latestMessage", "Latest chat message")
            val tone = parsed.optString("tone", "Playful & receptive")
            val vibeRead = parsed.optString("vibeRead", "Good banter with subtle attraction")
            val suggestedStrategy = parsed.optString("suggestedStrategy", "Keep it light and confident.")
            val otherPersonStatus = parsed.optString("otherPersonStatus", "They appear engaged and interested.")
            val rizzScore = parsed.optInt("rizzScore", 87)

            val metricsObj = parsed.optJSONObject("metrics")
            val metrics = ScreenshotMetrics(
                confidence = metricsObj?.optString("confidence", "High") ?: "High",
                naturalness = metricsObj?.optString("naturalness", "High") ?: "High",
                flirtLevel = metricsObj?.optString("flirtLevel", "Medium") ?: "Medium",
                cringeRisk = metricsObj?.optString("cringeRisk", "Low") ?: "Low",
                momentum = metricsObj?.optString("momentum", "Strong") ?: "Strong"
            )

            val responsesJson = parsed.optJSONArray("responses")
            val replies = mutableListOf<RizzReply>()
            if (responsesJson != null) {
                for (i in 0 until responsesJson.length()) {
                    val rObj = responsesJson.getJSONObject(i)
                    replies.add(
                        RizzReply(
                            text = rObj.optString("text"),
                            styleLabel = rObj.optString("styleLabel", "Best Pick"),
                            emoji = rObj.optString("emoji", "✨"),
                            isBestPick = rObj.optBoolean("isBestPick", i == 0),
                            tag = rObj.optString("tag", "Magnetic"),
                            score = ScoreBreakdown(
                                overall = (85..96).random(),
                                smoothness = (86..98).random(),
                                creativity = (80..95).random(),
                                confidence = (85..97).random(),
                                naturalness = (88..99).random(),
                                humor = (75..95).random()
                            )
                        )
                    )
                }
            }

            if (replies.isEmpty()) return@withContext null

            ScreenshotAnalysisResult(
                conversationSummary = conversationSummary,
                latestMessage = latestMessage,
                tone = tone,
                vibeRead = vibeRead,
                suggestedStrategy = suggestedStrategy,
                otherPersonStatus = otherPersonStatus,
                responses = replies,
                rizzScore = rizzScore,
                metrics = metrics
            )
        } catch (e: Exception) {
            Log.e(TAG, "Screenshot Vision analysis failed: ${e.message}", e)
            null
        }
    }

    /**
     * Refines a specific reply with a signature tone modifier
     */
    suspend fun refineText(original: String, modifier: ToneModifier): String? = withContext(Dispatchers.IO) {
        if (!isConfigured()) return@withContext null
        val prompt = """
            Take this dating reply: "$original"
            Remix it so that it is: ${modifier.label} (${modifier.emoji}).
            Keep it natural, human, and authentic. No cringe, no hashtags, no quotation marks.
            Output ONLY the single revised sentence.
        """.trimIndent()
        generatePrompt(prompt)
    }
}
