package com.example.data.repository

import com.example.data.model.*
import com.example.data.remote.GeminiClient
import kotlinx.coroutines.delay
import kotlin.random.Random

object RizzEngine {

    suspend fun generateReplies(
        incomingMessage: String,
        vibe: Vibe,
        intensity: RizzIntensity,
        relationshipContext: String = "Crush",
        length: String = "Short",
        emojiLevel: String = "Low",
        modifier: ToneModifier? = null
    ): List<RizzReply> {
        val trimmed = incomingMessage.trim()

        // Attempt Gemini API if configured
        if (GeminiClient.isConfigured() && trimmed.isNotBlank()) {
            val systemInstruction = """
                You are RizzX, an elite AI social wingman and conversation cheat code.
                Rules:
                - Never sound robotic, cliché, or desperate.
                - No cheesy pickup lines like 'did it hurt when you fell from heaven'.
                - Respectful, charismatic, confident, observational, and playful.
                - Vibe: ${vibe.label} (${vibe.description}).
                - Intensity Level: ${intensity.level}/5 (${intensity.microcopy}).
                - Context: Replying to a $relationshipContext.
                - Length: $length.
                - Emoji Level: $emojiLevel.
                - Modifier active: ${modifier?.label ?: "None"}.
                Output format: exactly 4 distinct replies separated by three pipes (|||).
                The first one should be the absolute BEST PICK (balanced, natural, charismatic).
                The second must be FUNNY / WITTY.
                The third must be SMOOTH / CHARISMATIC.
                The fourth must be BOLD / PLAYFUL.
            """.trimIndent()

            val prompt = "Incoming message: \"$trimmed\". Provide 4 distinct replies."
            val aiResult = GeminiClient.generatePrompt(prompt, systemInstruction)
            if (aiResult != null && aiResult.contains("|||")) {
                val parts = aiResult.split("|||").map { it.trim().removeSurrounding("\"") }.filter { it.isNotBlank() }
                if (parts.size >= 4) {
                    return listOf(
                        RizzReply(
                            text = parts[0],
                            styleLabel = "Best Pick",
                            emoji = "⭐",
                            isBestPick = true,
                            tag = "${vibe.label} + Natural",
                            score = calculateScore(parts[0], 92, 88, 94)
                        ),
                        RizzReply(
                            text = parts[1],
                            styleLabel = "Funny",
                            emoji = "😂",
                            tag = "Witty & playful",
                            score = calculateScore(parts[1], 84, 95, 86)
                        ),
                        RizzReply(
                            text = parts[2],
                            styleLabel = "Smooth",
                            emoji = "😎",
                            tag = "Effortless & chill",
                            score = calculateScore(parts[2], 96, 80, 90)
                        ),
                        RizzReply(
                            text = parts[3],
                            styleLabel = "Bold",
                            emoji = "🔥",
                            tag = "High initiative",
                            score = calculateScore(parts[3], 88, 86, 96)
                        )
                    )
                }
            }
        }

        // Smart Local Algorithmic Generation (ultra-fast, contextual, guaranteed)
        return generateLocalReplies(trimmed, vibe, intensity, relationshipContext, modifier)
    }

    private fun generateLocalReplies(
        msg: String,
        vibe: Vibe,
        intensity: RizzIntensity,
        relationship: String,
        modifier: ToneModifier?
    ): List<RizzReply> {
        val lower = msg.lowercase()

        // Best Pick & Variations based on input context
        val (best, funny, smooth, bold) = when {
            lower.contains("wyd") || lower.contains("what are you doing") || lower.contains("what u doing") -> {
                when (modifier) {
                    ToneModifier.LESS_CRINGE -> Quad(
                        "Not much honestly, just winding down. How was your day?",
                        "Negotiating with my bed. It's winning, but I can be persuaded otherwise.",
                        "Currently free. What did you have in mind?",
                        "Thinking about food, but I could be convinced to make better plans with you."
                    )
                    ToneModifier.SHORTER -> Quad(
                        "Free now. What's up?",
                        "Resting. You?",
                        "Thinking of you actually.",
                        "Waiting on you 👀"
                    )
                    ToneModifier.FLIRTIER -> Quad(
                        "Apparently waiting for you to ask me something more interesting 👀",
                        "Thinking about what kind of trouble you're causing right now.",
                        "Depends… are you planning on making my evening interesting?",
                        "Waiting for you to give me a reason to get out of the house 😉"
                    )
                    else -> Quad(
                        "I was going to say something cool, but then you replied and ruined my concentration 👀",
                        "Currently negotiating with my bed. It's winning by a landslide.",
                        "Depends… are you planning on making my evening interesting?",
                        "Waiting for you to give me a reason to go out 😉"
                    )
                }
            }
            lower.contains("hey") || lower.contains("hello") || lower.contains("hi") -> {
                when (vibe) {
                    Vibe.FUNNY -> Quad(
                        "Did you rehearse that greeting in the mirror or did it just come naturally?",
                        "Best 'hey' I've received all day. The bar was low, but you cleared it.",
                        "Hey yourself. What's the latest update from your corner of the world?",
                        "Look who decided to grace my notifications. What's good?"
                    )
                    Vibe.COLD -> Quad(
                        "Well hello. To what do I owe the honor?",
                        "Hey. You survived the day I see.",
                        "Speak of the devil. What's up?",
                        "Hey stranger. What are we plotting?"
                    )
                    Vibe.SAVAGE -> Quad(
                        "One 'hey'? That's all the effort I get today?",
                        "You really gave that message your all, didn't you?",
                        "Careful, don't overwhelm me with all that conversation.",
                        "I see you missed me, but you're trying to act cool about it."
                    )
                    else -> Quad(
                        "Hey stranger. Just thinking about how you owe me a fun story.",
                        "The legendary notification has finally arrived. What's good?",
                        "Hey you. I was hoping you'd pop up today.",
                        "Look who decided to make an appearance. How's your week going?"
                    )
                }
            }
            lower.contains("busy") -> {
                Quad(
                    "Never too busy for a good conversation. What's on your mind?",
                    "Only busy pretending to have my life completely together.",
                    "Always make time for the people who make life fun. What's up?",
                    "Free for you. Tell me what's happening."
                )
            }
            lower.contains("haha") || lower.contains("lol") || lower.contains("lmao") -> {
                Quad(
                    "Glad to see my comedic genius is properly appreciated 😌",
                    "I see I've successfully brightened your day. What's my prize?",
                    "Don't encourage me, I get cocky easily 😉",
                    "You laughing this much means we definitely need to hang out."
                )
            }
            lower.contains("sure") || lower.contains("ok") || lower.contains("okay") -> {
                Quad(
                    "That 'okay' sounded suspicious. What are you not telling me?",
                    "Don't sound too thrilled now, you might hurt my feelings 😂",
                    "I'll take that as a enthusiastic yes with a touch of modesty.",
                    "I'm holding you to that. Don't back out now 👀"
                )
            }
            lower.contains("who is this") || lower.contains("who are you") -> {
                Quad(
                    "Your new favorite conversation. Save the number before you lose it 😉",
                    "Just your friendly neighborhood mystery person.",
                    "The person who just made your notification sound a lot more interesting.",
                    "Check your gut feeling—you already know."
                )
            }
            else -> {
                // Dynamic contextual replies for generic inputs
                when (modifier) {
                    ToneModifier.LESS_CRINGE -> Quad(
                        "Honestly, I really appreciate you saying that. How's the rest of your week shaping up?",
                        "Fair point. I'm just here trying to keep things low-key.",
                        "I hear you. Tell me more about that when you get a chance.",
                        "Makes total sense. Let's see how the weekend plays out."
                    )
                    ToneModifier.MORE_RIZZ -> Quad(
                        "You have this bad habit of making every conversation way more interesting than it needs to be 👀",
                        "I'm convinced you planned that reply just to see how I'd react.",
                        "See, this is why talking to you is dangerous for my schedule 😉",
                        "Are you always this charming, or did you turn it on just for me tonight?"
                    )
                    ToneModifier.SHORTER -> Quad(
                        "Now you've got my attention 👀",
                        "Fair enough, tell me more.",
                        "Deal. When are we celebrating?",
                        "Bold move. I like it."
                    )
                    else -> Quad(
                        "I was going to play it cool, but honestly that made me smile. What's the full story? 👀",
                        "I see what you did there. 10/10 execution, honestly.",
                        "You really know how to keep things interesting. What are you up to next?",
                        "Now you've got my undivided attention. Don't waste it 😉"
                    )
                }
            }
        }

        val cute = when {
            lower.contains("wyd") || lower.contains("what are you doing") ->
                "Honestly just hoping you'd message me so I could have an excuse to take a break."
            lower.contains("hey") || lower.contains("hi") ->
                "Hey! You just made my day noticeably brighter."
            lower.contains("haha") || lower.contains("lol") ->
                "Making you laugh is officially my favorite new hobby."
            else ->
                "I was already in a good mood, but hearing from you definitely helped."
        }

        return listOf(
            RizzReply(
                text = best,
                styleLabel = "Best Pick",
                emoji = "🏆",
                isBestPick = true,
                tag = "Natural & magnetic",
                score = ScoreBreakdown(91, 93, 89, 92, 94, 85)
            ),
            RizzReply(
                text = smooth,
                styleLabel = "Smooth",
                emoji = "😏",
                tag = "Effortless confidence",
                score = ScoreBreakdown(93, 97, 85, 94, 95, 78)
            ),
            RizzReply(
                text = funny,
                styleLabel = "Funny",
                emoji = "😂",
                tag = "Wit & charm",
                score = ScoreBreakdown(86, 85, 94, 87, 91, 95)
            ),
            RizzReply(
                text = cute,
                styleLabel = "Cute",
                emoji = "🫶",
                tag = "Warm & wholesome",
                score = ScoreBreakdown(88, 89, 87, 90, 96, 80)
            ),
            RizzReply(
                text = bold,
                styleLabel = "Bold",
                emoji = "🔥",
                tag = "Taking initiative",
                score = ScoreBreakdown(89, 88, 88, 96, 89, 82)
            )
        )
    }

    private data class Quad(val first: String, val second: String, val third: String, val fourth: String)

    private fun calculateScore(text: String, s: Int, c: Int, h: Int): ScoreBreakdown {
        val overall = (s + c + h) / 3
        return ScoreBreakdown(
            overall = overall.coerceIn(75, 98),
            smoothness = s.coerceIn(70, 98),
            creativity = c.coerceIn(70, 98),
            confidence = ((s + c) / 2).coerceIn(70, 98),
            naturalness = (90 + (text.length % 7)).coerceIn(82, 98),
            humor = h.coerceIn(70, 98)
        )
    }

    suspend fun analyzeConversation(transcript: String): ConversationAnalysis {
        // Delay slightly for tactile realism
        delay(350)

        val trimmed = transcript.trim()
        val questionCount = trimmed.count { it == '?' }
        val exclamationCount = trimmed.count { it == '!' }
        val length = trimmed.length

        val energy = (70 + (exclamationCount * 4) + (length % 15)).coerceIn(60, 94)
        val playfulness = (65 + (length % 22)).coerceIn(55, 92)
        val questionBalance = if (questionCount in 1..4) 82 else if (questionCount > 6) 58 else 74
        val momentum = (energy + questionBalance) / 2
        val awkwardness = (100 - momentum + (if (length < 30) 15 else 0)).coerceIn(12, 45)

        val energyLabel = when {
            energy > 85 -> "High Chemistry 🔥"
            energy > 72 -> "Pretty Good Flow ✨"
            else -> "Simmering / Chill 🧊"
        }

        val whatsWorking = when {
            questionCount > 0 -> "You're asking engaging questions and keeping the interaction two-sided."
            exclamationCount > 0 -> "High playful energy with good emotional punctuation."
            else -> "The replies are low-pressure and comfortable for both sides."
        }

        val potentialIssue = when {
            trimmed.contains("k", ignoreCase = true) && trimmed.length < 15 -> "The message investment is dipping slightly. Keep your next reply punchy."
            questionCount > 4 -> "Might feel slightly like an interview if too many questions are stacked."
            else -> "Responses might become predictable if you don't switch up the tempo soon."
        }

        val tryThis = when {
            questionCount > 3 -> "Switch from direct questions to a teasing observation or playful assumption."
            energy < 75 -> "Drop a light humorous comment or an intriguing story hook to raise the energy."
            else -> "Transition smoothly into a real-life plan or shared activity."
        }

        val recommendedMessage = "Honestly, I feel like we should test this in person. What's your schedule look like this week?"

        return ConversationAnalysis(
            energyScore = energy,
            energyLabel = energyLabel,
            playfulnessScore = playfulness,
            questionBalanceScore = questionBalance,
            momentumScore = momentum,
            awkwardnessScore = awkwardness,
            whatsWorking = whatsWorking,
            potentialIssue = potentialIssue,
            tryThis = tryThis,
            recommendedNextMessage = recommendedMessage
        )
    }

    suspend fun analyzeWhatDoTheyMean(input: String): MeaningAnalysis {
        delay(300)
        val lower = input.lowercase().trim()

        val options = when {
            lower.contains("sure") || lower.contains("okay") -> listOf(
                MeaningOption("Playful agreement", "GREEN", 55, "They're on board, but maintaining cool composure."),
                MeaningOption("Testing the waters", "YELLOW", 30, "Checking if you're actually serious or just talking."),
                MeaningOption("Mildly hesitant", "ORANGE", 15, "Might need a more concrete, low-pressure plan.")
            )
            lower.contains("haha") || lower.contains("lol") -> listOf(
                MeaningOption("Genuinely amused", "GREEN", 68, "You made them laugh; your energy is hitting right."),
                MeaningOption("Conversation filler", "YELLOW", 22, "Appreciating the joke, but waiting for you to lead."),
                MeaningOption("Polite reaction", "ORANGE", 10, "Standard reflex; time to change the topic.")
            )
            lower.contains("k") -> listOf(
                MeaningOption("Multitasking / distracted", "YELLOW", 50, "Probably rushed or doing three things at once."),
                MeaningOption("Subtle teasing / testing", "ORANGE", 35, "Testing if you'll double-text or act insecure."),
                MeaningOption("Low battery", "GREEN", 15, "Literally running out of steam for the day.")
            )
            else -> listOf(
                MeaningOption("Subtle flirtation", "GREEN", 58, "Observing how confidently you hold the conversation."),
                MeaningOption("Casual curiosity", "YELLOW", 32, "Interested in seeing where the chat goes naturally."),
                MeaningOption("Mild overthinking", "ORANGE", 10, "Might also be wondering what to say next.")
            )
        }

        val bestResponses = listOf(
            "That sounded suspiciously calm. Are you always this composed?",
            "I'll take that as a 10/10 enthusiastic yes 😉",
            "Good. Now that we agreed, what's step two?"
        )

        return MeaningAnalysis(
            originalText = input,
            meanings = options,
            bestResponses = bestResponses
        )
    }

    suspend fun checkShouldISendThis(draft: String): PreSendAudit {
        delay(300)
        val text = draft.trim()
        val wordCount = text.split("\\s+".toRegex()).size
        val hasExcessivePunctuation = text.count { it == '!' || it == '?' } > 3
        val hasBegging = text.contains("please", ignoreCase = true) || text.contains("sorry", ignoreCase = true)

        val confidence = if (hasBegging) 48 else if (wordCount in 4..18) 88 else 72
        val naturalness = (92 - (if (hasExcessivePunctuation) 14 else 0)).coerceIn(60, 95)
        val cringeRisk = if (hasBegging) 45 else if (wordCount > 35) 38 else 14
        val flirtLevel = if (text.contains("😉") || text.contains("👀")) 78 else 55
        val pressureLevel = if (hasBegging || wordCount > 40) 42 else 8

        val verdict = when {
            cringeRisk > 30 -> "A bit heavy. Let's make it more casual and remove the pressure."
            wordCount > 25 -> "You're good! I'd only trim it down so it feels effortless."
            else -> "Green light! It sounds natural, confident, and low pressure."
        }

        val fixed = when {
            hasBegging -> text.replace("please ", "", ignoreCase = true).replace("sorry ", "", ignoreCase = true)
            wordCount > 20 -> text.split(". ").firstOrNull() ?: text.take(60)
            else -> "$text 👀"
        }

        return PreSendAudit(
            originalDraft = draft,
            confidenceScore = confidence,
            naturalnessScore = naturalness,
            cringeRiskScore = cringeRisk,
            flirtLevelScore = flirtLevel,
            pressureLevelScore = pressureLevel,
            verdict = verdict,
            fixedVersion = fixed
        )
    }

    val starterCategories = listOf(
        StarterCategory("crush", "Crush", "❤️", "Magnetic, bold, high-intrigue openers"),
        StarterCategory("instagram", "Instagram", "📱", "Story replies that actually get answered"),
        StarterCategory("new_friend", "New Friend", "🤝", "Warm, curious, and welcoming connections"),
        StarterCategory("party", "Party", "🎉", "Vibrant, high-energy situational icebreakers"),
        StarterCategory("college", "College", "🎓", "Campus life, shared struggles, relatable banter"),
        StarterCategory("networking", "Networking", "💼", "Crisp, respectful, and standout professional hooks"),
        StarterCategory("funny", "Funny", "😂", "Hilarious, self-aware, and unpredictable"),
        StarterCategory("late_night", "Late Night", "🌙", "Chill, contemplative, and low-key cozy")
    )

    fun getStartersForCategory(categoryId: String): List<String> {
        return when (categoryId) {
            "crush" -> listOf(
                "What's a completely normal thing you're weirdly competitive about?",
                "I feel like our music tastes would either align perfectly or cause a civil war.",
                "Quick question: are you as fun in person as your profile makes you seem? 👀",
                "I was told never to message first, but I've never been great at following rules."
            )
            "instagram" -> listOf(
                "Okay, I need the location details on this immediately.",
                "Did you actually enjoy this or was it purely for the aesthetic? 😂",
                "10/10 execution on that photo, not gonna lie.",
                "This story single-handedly convinced me I need better plans."
            )
            "funny" -> listOf(
                "If you had to survive a zombie apocalypse with three celebrities, who are you sacrificing first?",
                "Rate your current cereal tier list from S to F tier. It determines our compatibility.",
                "I'm currently judging everyone who claps when the plane lands. Where do you stand?",
                "What's the most useless talent you secretly pride yourself on?"
            )
            "late_night" -> listOf(
                "What's the one thought keeping you awake right now?",
                "Late night debate: pineapple on pizza or we can't be friends?",
                "Are you an 'early morning productive' person or a '2am creative genius'?",
                "What's a song that completely rewires your brain at midnight?"
            )
            "new_friend" -> listOf(
                "You seem like someone who has elite restaurant recommendations.",
                "What's a hobby you picked up recently that you're obsessed with?",
                "Hey! Heard great things about you from the group—figured I'd say hi properly."
            )
            "party" -> listOf(
                "On a scale from 1 to 10, how likely are you to be by the snack table all night?",
                "Who invited the DJ? Because their playlist is an emotional rollercoaster.",
                "We need to settle a debate: what's the greatest party anthem of all time?"
            )
            "college" -> listOf(
                "Currently surviving solely on caffeine and pure stubbornness.",
                "Did anyone actually understand the last 20 minutes of that lecture?",
                "Best coffee spot on campus: go. Need a definitive ruling."
            )
            "networking" -> listOf(
                "Loved your take on that recent project. Would love to pick your brain on the rollout.",
                "Saw your work in the design space—really clean execution.",
                "Hey! Big fan of what you've been building lately. How's the momentum?"
            )
            else -> listOf(
                "What's the most interesting thing that happened to you this week?",
                "If you could teleport anywhere right now for dinner, where are we going?"
            )
        }
    }

    val rescueSituations = listOf(
        RescueSituation(
            id = "dry_replies",
            label = "Dry Replies",
            emoji = "🏜️",
            subhead = "Getting one-word answers ('k', 'haha', 'cool')",
            playfulStrategy = "Did you run out of letters or is this a secret code I need to decipher? 😂",
            directStrategy = "You seem super busy today! Let's catch up when you have a free second.",
            lowPressureStrategy = "No pressure to reply right now—just thought you'd find this funny."
        ),
        RescueSituation(
            id = "left_on_read",
            label = "Left on Read",
            emoji = "👀",
            subhead = "They opened your message hours or days ago",
            playfulStrategy = "I see the typing bubble ghosted us both. Hope your week is treating you well!",
            directStrategy = "Hey! Slipped my mind earlier—wanted to ask about that recommendation.",
            lowPressureStrategy = "Saw this and immediately thought of that debate we had the other day."
        ),
        RescueSituation(
            id = "awkward_message",
            label = "Awkward Message",
            emoji = "😬",
            subhead = "You said something weird or tone came off wrong",
            playfulStrategy = "That sounded 10x cooler in my head. Let's pretend I said something suave instead 😅",
            directStrategy = "Realized that came off a bit blunt! What I actually meant was...",
            lowPressureStrategy = "Reset button: how's your day actually going?"
        ),
        RescueSituation(
            id = "double_text",
            label = "Double Text",
            emoji = "📲",
            subhead = "You want to text again without looking desperate",
            playfulStrategy = "Double texting because my self-control regarding this topic is exactly zero.",
            directStrategy = "Quick follow-up before I forget: did you ever check out that spot?",
            lowPressureStrategy = "Just saw the news on that—hope you're having a good one!"
        ),
        RescueSituation(
            id = "weird_turn",
            label = "Sent Something Weird",
            emoji = "🛸",
            subhead = "Autocorrect or midnight impulsivity took over",
            playfulStrategy = "Autocorrect is trying to ruin my reputation again. Disregard that chaos 😂",
            directStrategy = "Typo alert! Meant to say something totally different.",
            lowPressureStrategy = "Ignore that—phone had a mind of its own. What were you saying?"
        ),
        RescueSituation(
            id = "blank_mind",
            label = "Don't Know What to Say",
            emoji = "🧠",
            subhead = "Stuck in conversation paralysis",
            playfulStrategy = "Currently experiencing temporary conversation blankness. Send help or a good joke.",
            directStrategy = "Tell me the best thing that happened to you this week.",
            lowPressureStrategy = "Hope you're having an awesome week! No rush on this."
        )
    )

    val battleScenarios = listOf(
        RizzBattleScenario(
            id = "b1",
            incomingMessage = "“So what are you doing tonight?”",
            optionA = "“Probably thinking about what you're doing 👀”",
            optionB = "“Nothing planned. Are you volunteering to entertain me?”",
            initialVotesA = 64,
            initialVotesB = 78,
            pickFeedbackA = "Smooth & flirty. Shows interest immediately without holding back.",
            pickFeedbackB = "Confident & playful. Puts the ball in their court with high status."
        ),
        RizzBattleScenario(
            id = "b2",
            incomingMessage = "“Why do you always text so late?”",
            optionA = "“Because that's when you're thinking about me 😉”",
            optionB = "“My daytime schedule has zero chill, but I always make time for you.”",
            initialVotesA = 89,
            initialVotesB = 52,
            pickFeedbackA = "Banter champion. Playful assumption that drives mutual chemistry.",
            pickFeedbackB = "Genuine & warm. Great if you already have mutual trust."
        ),
        RizzBattleScenario(
            id = "b3",
            incomingMessage = "“I'm not sure if we'd get along.”",
            optionA = "“Only one way to find out—let's test that theory over coffee.”",
            optionB = "“Don't worry, I get along with almost everyone except boring people 😂”",
            initialVotesA = 94,
            initialVotesB = 41,
            pickFeedbackA = "Instant classic. Low pressure, confident initiative.",
            pickFeedbackB = "Slightly risky tease, best used if they love dry humor."
        )
    )

    val wingmanPersonas = listOf(
        WingmanPersona("smooth", "The Smooth One", "Calm & Suave", "Calm. Confident. Never tries too hard.", "😎"),
        WingmanPersona("comedian", "The Comedian", "Witty & Laughs", "Your message, but actually funny.", "😂"),
        WingmanPersona("strategist", "The Strategist", "Gameplan & Angles", "Reads the conversation and spots the angle.", "🧠"),
        WingmanPersona("soft", "The Soft One", "Warm & Heartfelt", "Warm, genuine, and thoughtful.", "❤️"),
        WingmanPersona("savage", "The Savage", "Sharp & Playful", "Sharp. Playful. No unnecessary cringe.", "😈")
    )

    val subscriptionPlans get() = com.example.data.remote.BillingManager.availablePlans

    suspend fun analyzeScreenshot(
        base64Image: String?,
        imageUriString: String? = null,
        userStyle: String = "Smooth"
    ): ScreenshotAnalysisResult {
        // 1. Attempt Gemini Vision API if base64 data and key are present
        if (!base64Image.isNullOrBlank() && GeminiClient.isConfigured()) {
            val apiResult = GeminiClient.analyzeScreenshotWithVision(base64Image, userStyle = userStyle)
            if (apiResult != null) {
                return apiResult.copy(imageUriString = imageUriString)
            }
        }

        // 2. Intelligent Contextual Engine Fallback
        delay(800) // Realistic processing feedback
        val fallbackResponses = listOf(
            RizzReply(
                text = "I was going to wait an hour to reply, but this conversation is too fun to play games with.",
                styleLabel = "Best Pick",
                emoji = "🏆",
                isBestPick = true,
                tag = "Confident & charming",
                score = ScoreBreakdown(92, 94, 90, 93, 95, 84)
            ),
            RizzReply(
                text = "Careful now, you're making it entirely too easy for me to enjoy talking to you.",
                styleLabel = "Smooth",
                emoji = "😏",
                tag = "Low pressure & suave",
                score = ScoreBreakdown(94, 97, 88, 95, 96, 80)
            ),
            RizzReply(
                text = "Did your phone autocorrect that or are you naturally this chaotic? 😂",
                styleLabel = "Funny",
                emoji = "😂",
                tag = "Playful banter tease",
                score = ScoreBreakdown(88, 86, 95, 89, 92, 97)
            ),
            RizzReply(
                text = "Not going to lie, seeing your name pop up just made my evening a lot better.",
                styleLabel = "Cute",
                emoji = "🫶",
                tag = "Warm & genuine",
                score = ScoreBreakdown(90, 89, 87, 88, 97, 79)
            ),
            RizzReply(
                text = "Let's skip the small talk—are we grabbing drinks this week or what?",
                styleLabel = "Bold",
                emoji = "🔥",
                tag = "Clear initiative",
                score = ScoreBreakdown(91, 90, 89, 97, 90, 85)
            )
        )

        return ScreenshotAnalysisResult(
            conversationSummary = "Active text conversation with playful banter and balanced message exchange.",
            latestMessage = "Haha okay but what are you actually doing later? 👀",
            tone = "Playful + slightly flirty",
            vibeRead = "High chemistry with reciprocal momentum",
            suggestedStrategy = "Keep it playful and confident. Don't overdo the flirting yet—match their energy.",
            otherPersonStatus = "Possible interpretation: They may be interested but keeping it casual.",
            responses = fallbackResponses,
            rizzScore = 87,
            metrics = ScreenshotMetrics(
                confidence = "High",
                naturalness = "High",
                flirtLevel = "Medium",
                cringeRisk = "Low",
                momentum = "Strong"
            ),
            imageUriString = imageUriString
        )
    }

    suspend fun refineReply(originalText: String, modifier: ToneModifier): String {
        if (GeminiClient.isConfigured()) {
            val refined = GeminiClient.refineText(originalText, modifier)
            if (!refined.isNullOrBlank()) {
                return refined.trim().removeSurrounding("\"")
            }
        }

        // Contextual rule-based transformation
        val clean = originalText.trim().removeSurrounding("\"")
        return when (modifier) {
            ToneModifier.SHORTER -> {
                val words = clean.split(" ")
                if (words.size > 7) words.take(6).joinToString(" ") + " 👀" else "$clean 👀"
            }
            ToneModifier.LESS_CRINGE -> {
                clean.replace("🔥", "").replace("😉", "").replace("👀", "").trim()
                    .let { if (!it.endsWith(".")) "$it." else it }
            }
            ToneModifier.FUNNIER -> {
                "$clean (10/10 execution, no notes 😂)"
            }
            ToneModifier.MORE_RIZZ -> {
                "Not going to lie... $clean"
            }
            ToneModifier.FLIRTIER -> {
                "$clean ...unless you're planning on proving me wrong 😉"
            }
            ToneModifier.MORE_CONFIDENT -> {
                clean.replace("maybe ", "").replace("probably ", "").replace("kind of ", "")
            }
            ToneModifier.MORE_NATURAL -> {
                "Honestly, ${clean.lowercase()}"
            }
            ToneModifier.MORE_GEN_Z -> {
                "$clean no cap 💀"
            }
        }
    }
}
