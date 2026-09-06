package com.example.aegis.engine

import kotlinx.coroutines.delay
import kotlin.random.Random

data class ParsedCommand(
    val action: AgentAction,
    val rawQuery: String,
    val targetUrl: String? = null,
    val recipientEmail: String? = null,
    val subject: String? = null,
    val body: String? = null,
    val topicOrContent: String? = null
)

enum class AgentAction {
    POST,
    CONNECT,
    INBOX,
    PROFILE,
    EMAIL,
    SETUP,
    STATUS,
    HELP,
    UNKNOWN
}

object AegisAiEngine {

    fun parseUserCommand(input: String): ParsedCommand {
        val trimmed = input.trim()
        val lower = trimmed.lowercase()

        // 1. Post command
        if (lower.startsWith("post:") || lower.startsWith("post ") || lower.contains("publish to feed") || lower.contains("post content") || (lower.startsWith("post") && trimmed.length > 5)) {
            val content = when {
                trimmed.startsWith("post:", ignoreCase = true) -> trimmed.substring(5).trim()
                trimmed.startsWith("post", ignoreCase = true) -> trimmed.substring(4).trim()
                else -> trimmed
            }
            return ParsedCommand(
                action = AgentAction.POST,
                rawQuery = trimmed,
                topicOrContent = content.ifBlank { "Excited to share our latest milestone in AI autonomous systems and agent workflows!" }
            )
        }

        // 2. Connect command
        if (lower.contains("connect") || lower.contains("request") || lower.contains("invite")) {
            val urlRegex = Regex("""https://[^\s]+""")
            val match = urlRegex.find(trimmed)?.value
            return ParsedCommand(
                action = AgentAction.CONNECT,
                rawQuery = trimmed,
                targetUrl = match ?: if (lower.contains("linkedin.com")) trimmed else null
            )
        }

        // 3. Inbox / DM command
        if (lower.contains("inbox") || lower.contains("message") || lower.contains("reply") || lower.contains("sweep") || lower.contains("dm")) {
            return ParsedCommand(
                action = AgentAction.INBOX,
                rawQuery = trimmed
            )
        }

        // 4. Profile / Optimize command
        if (lower.contains("profile") || lower.contains("optimize") || lower.contains("headline") || lower.contains("about section") || lower.contains("bio")) {
            return ParsedCommand(
                action = AgentAction.PROFILE,
                rawQuery = trimmed
            )
        }

        // 5. Email command
        if (lower.contains("email") || lower.contains("send email") || lower.contains("mail")) {
            val emailRegex = Regex("""[\w.\-]+@[\w.\-]+\.\w+""")
            val emailMatch = emailRegex.find(trimmed)?.value
            
            var subject: String? = null
            var body: String? = null
            
            if (trimmed.contains("subject:", ignoreCase = true)) {
                val afterSubject = trimmed.substring(trimmed.indexOf("subject:", ignoreCase = true) + 8)
                if (afterSubject.contains("body:", ignoreCase = true)) {
                    subject = afterSubject.substring(0, afterSubject.indexOf("body:", ignoreCase = true)).trim()
                    body = afterSubject.substring(afterSubject.indexOf("body:", ignoreCase = true) + 5).trim()
                } else {
                    subject = afterSubject.trim()
                }
            }

            return ParsedCommand(
                action = AgentAction.EMAIL,
                rawQuery = trimmed,
                recipientEmail = emailMatch,
                subject = subject ?: "Aegis AI Agent: System Notification",
                body = body ?: "Automated verification payload delivered via Aegis SMTP Engine."
            )
        }

        // 6. Setup / Config
        if (lower.contains("setup") || lower.contains("config") || lower.contains("credential") || lower.contains("cookie") || lower.contains("api key")) {
            return ParsedCommand(action = AgentAction.SETUP, rawQuery = trimmed)
        }

        // 7. Status
        if (lower.contains("status") || lower.contains("health") || lower.contains("check connection")) {
            return ParsedCommand(action = AgentAction.STATUS, rawQuery = trimmed)
        }

        // 8. Help
        if (lower.contains("help") || lower == "?" || lower.contains("what can you do") || lower.contains("features")) {
            return ParsedCommand(action = AgentAction.HELP, rawQuery = trimmed)
        }

        return ParsedCommand(action = AgentAction.UNKNOWN, rawQuery = trimmed)
    }

    suspend fun generatePost(
        topic: String,
        tone: String = "Thought Leadership",
        groqApiKey: String? = null,
        groqModel: String = "llama-3.3-70b-versatile"
    ): Pair<String, String> {
        val cleanTopic = topic.trim()

        if (!groqApiKey.isNullOrBlank()) {
            val systemPrompt = "You are Aegis, an elite LinkedIn ghostwriter and B2B growth strategist. Create high-engagement, viral-formatted LinkedIn posts with hook, body points, call-to-action, and relevant hashtags at the bottom."
            val userPrompt = "Write a top-tier LinkedIn post on: \"$cleanTopic\". Tone: $tone. Keep it concise, punchy with line breaks, emojis, and 3-5 relevant hashtags at the very end."
            val groqResult = GroqApiClient.generateCompletion(
                apiKey = groqApiKey,
                model = groqModel,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                maxTokens = 600
            )
            if (groqResult.isSuccess) {
                val fullPost = groqResult.getOrThrow()
                val hashtagRegex = Regex("""#\w+""")
                val tags = hashtagRegex.findAll(fullPost).map { it.value }.joinToString(" ")
                return Pair(fullPost, tags.ifBlank { "#AIAgents #Automation #Innovation #Tech" })
            }
        }

        delay(600)
        val hashtags = when {
            cleanTopic.contains("ai", ignoreCase = true) -> "#AIAgents #ArtificialIntelligence #Automation #FutureOfWork #MachineLearning"
            cleanTopic.contains("build", ignoreCase = true) || cleanTopic.contains("product", ignoreCase = true) -> "#BuildingInPublic #ProductStrategy #TechInnovation #StartupLife"
            cleanTopic.contains("lead", ignoreCase = true) || cleanTopic.contains("growth", ignoreCase = true) -> "#Leadership #GrowthMindset #ExecutiveStrategy #CareerGrowth"
            else -> "#LinkedInGrowth #AIAutomation #Engineering #Innovation #Technology"
        }

        val post = when (tone) {
            "Actionable Insights" -> """
🚀 3 Key Lessons on scaling autonomous workflows effectively:

1️⃣ Frictionless Handshakes: Keep automation human-centered with real-time feedback loops.
2️⃣ Context is King: Tailor messaging to prospective synergies rather than generic templates.
3️⃣ Precision Over Volume: 10 deeply relevant touches outperform 500 blind blasts every time.

How are you approaching automated workflows in your team this quarter?

$hashtags
            """.trimIndent()

            "Storytelling / Journey" -> """
Reflecting on where we started vs. where we are today with autonomous systems:

Six months ago, managing outreach and communication felt like a 24/7 bottleneck. Today, with Aegis autonomous agents handling data validation, scheduled dispatches, and intelligent inbox filtering, our focus is 100% on high-leverage creative strategy.

The future of productivity isn't about replacing humans—it's about supercharging our highest-value thinking. 💡

$hashtags
            """.trimIndent()

            else -> """
$cleanTopic

Deploying intelligent agent frameworks has completely transformed how modern teams scale their presence and inbound conversations. By integrating autonomous checks with human supervision, we keep engagement natural, authentic, and high-impact. 🤖✨

Excited to see where this wave of agentic software takes the industry next!

$hashtags
            """.trimIndent()
        }

        return Pair(post, hashtags)
    }

    suspend fun generateConnectionNote(
        fullName: String,
        headline: String,
        company: String,
        groqApiKey: String? = null,
        groqModel: String = "llama-3.3-70b-versatile"
    ): String {
        val firstName = fullName.split(" ").firstOrNull() ?: fullName

        if (!groqApiKey.isNullOrBlank()) {
            val systemPrompt = "You are an AI outreach specialist. Write a warm, authentic 1-2 sentence LinkedIn connection request note under 280 characters."
            val userPrompt = "Write a personalized LinkedIn connection note for $fullName ($headline at $company). Keep under 280 characters."
            val groqResult = GroqApiClient.generateCompletion(
                apiKey = groqApiKey,
                model = groqModel,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                maxTokens = 120
            )
            if (groqResult.isSuccess) {
                val note = groqResult.getOrThrow()
                return if (note.length > 295) note.substring(0, 290) + "..." else note
            }
        }

        delay(500)
        val templates = listOf(
            "Hi $firstName, loved your work on $headline${if (company.isNotBlank()) " at $company" else ""}. Would love to connect and follow your journey here on LinkedIn!",
            "Hi $firstName, noticed your background in $headline. Always keen to connect with fellow leaders in this space and exchange insights!",
            "Hello $firstName, impressive trajectory with $headline. Would be great to add you to my network and stay in touch on industry trends."
        )
        return templates[Random.nextInt(templates.size)]
    }

    suspend fun generateInboxReply(
        senderName: String,
        inboundMessage: String,
        groqApiKey: String? = null,
        groqModel: String = "llama-3.3-70b-versatile"
    ): String {
        val firstName = senderName.split(" ").firstOrNull() ?: senderName

        if (!groqApiKey.isNullOrBlank()) {
            val systemPrompt = "You are an executive assistant managing LinkedIn DMs. Write a polite, high-converting, professional response in 2-3 sentences."
            val userPrompt = "Generate a reply to this LinkedIn DM from $senderName: \"$inboundMessage\""
            val groqResult = GroqApiClient.generateCompletion(
                apiKey = groqApiKey,
                model = groqModel,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                maxTokens = 200
            )
            if (groqResult.isSuccess) {
                return groqResult.getOrThrow()
            }
        }

        delay(700)
        val lower = inboundMessage.lowercase()
        return when {
            lower.contains("call") || lower.contains("meeting") || lower.contains("time") -> 
                "Hi $firstName, thanks for reaching out! I'd be happy to discuss further. Let me check my schedule for early next week and send over a few slots that work."
            lower.contains("demo") || lower.contains("product") || lower.contains("pricing") -> 
                "Hi $firstName, appreciate your interest! I'm reviewing your note and will follow up with detailed documentation and a walkthrough shortly."
            lower.contains("connect") || lower.contains("congrats") || lower.contains("great") -> 
                "Thanks so much, $firstName! Really appreciate the kind words and glad to be connected. Let's definitely keep in touch!"
            else -> 
                "Hi $firstName, thanks for reaching out! I've received your note and will review it closely. Looking forward to continuing the conversation."
        }
    }

    suspend fun optimizeProfile(
        currentHeadline: String,
        currentAbout: String,
        groqApiKey: String? = null,
        groqModel: String = "llama-3.3-70b-versatile"
    ): Pair<String, String> {
        if (!groqApiKey.isNullOrBlank()) {
            val systemPrompt = "You are a LinkedIn profile optimization expert. Return an optimized headline and about section."
            val userPrompt = "Optimize this LinkedIn profile:\nCurrent Headline: $currentHeadline\nCurrent About: $currentAbout\n\nReturn in this exact format:\nHEADLINE: <optimized headline under 120 chars>\nABOUT: <optimized about section>"
            val groqResult = GroqApiClient.generateCompletion(
                apiKey = groqApiKey,
                model = groqModel,
                systemPrompt = systemPrompt,
                userPrompt = userPrompt,
                maxTokens = 500
            )
            if (groqResult.isSuccess) {
                val output = groqResult.getOrThrow()
                if (output.contains("HEADLINE:") && output.contains("ABOUT:")) {
                    val head = output.substringAfter("HEADLINE:").substringBefore("ABOUT:").trim()
                    val abt = output.substringAfter("ABOUT:").trim()
                    return Pair(head, abt)
                }
            }
        }

        delay(900)
        val optimizedHeadline = if (currentHeadline.isNotBlank() && currentHeadline.length > 5) {
            "🚀 Building Autonomous AI Agents | Senior Tech Strategist | Scaling Systems & Product Growth | Speaker & Advisor"
        } else {
            "AI Engineering & Autonomous Automation Lead | Building Scalable Agent Architectures | Tech Innovator"
        }

        val optimizedAbout = """
I specialize in bridging cutting-edge AI agent frameworks with high-velocity product execution. Over the past several years, I've helped scale automated systems, modernize outreach pipelines, and empower engineering teams with autonomous tooling.

Core Expertise:
• Autonomous Agent Architectures & Workflow Automation
• Scalable Cloud & Modern Android / Full-Stack Systems
• Data-Driven Growth, Strategy & High-Impact Leadership

Always eager to connect with innovators, founders, and engineers pushing the frontiers of applied AI. Feel free to send a connection request or direct message!
        """.trimIndent()

        return Pair(optimizedHeadline, optimizedAbout)
    }
}
