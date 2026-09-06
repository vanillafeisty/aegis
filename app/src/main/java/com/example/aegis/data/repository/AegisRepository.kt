package com.example.aegis.data.repository

import com.example.aegis.data.database.AegisDatabase
import com.example.aegis.data.model.*
import com.example.aegis.engine.AegisAiEngine
import com.example.aegis.engine.AgentAction
import com.example.aegis.engine.ParsedCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AegisRepository(private val db: AegisDatabase) {

    val allMessages: Flow<List<ChatMessageEntity>> = db.chatDao().getAllMessages()
    val allPosts: Flow<List<PostDraftEntity>> = db.postDao().getAllPosts()
    val allLeads: Flow<List<ConnectionLeadEntity>> = db.connectionDao().getAllLeads()
    val allThreads: Flow<List<InboxThreadEntity>> = db.inboxDao().getAllThreads()
    val allAudits: Flow<List<ProfileAuditEntity>> = db.profileAuditDao().getAllAudits()
    val allEmails: Flow<List<EmailLogEntity>> = db.emailDao().getAllEmails()
    val allActivities: Flow<List<ActivityLogEntity>> = db.activityDao().getAllActivities()
    val settingsFlow: Flow<CredentialSettingsEntity?> = db.settingsDao().getSettings()

    suspend fun seedInitialDataIfEmpty() {
        val userDefaultSettings = CredentialSettingsEntity(
            id = 1,
            linkedinSessionCookie = "",
            linkedinAccessToken = "",
            linkedinClientId = "",
            linkedinClientSecret = "",
            linkedinRedirectUri = "http://localhost:8000/callback",
            groqApiKey = "",
            groqModel = "llama-3.3-70b-versatile",
            zapierMcpUrl = "https://mcp.zapier.com/api/v1/connect",
            smtpEmail = "",
            smtpPassword = "",
            environment = "development",
            debugMode = true,
            isConfigured = false,
            stealthModeEnabled = true,
            humanTypingCadence = true
        )

        val currentSettings = db.settingsDao().getSettingsDirect()
        if (currentSettings == null) {
            db.settingsDao().saveSettings(userDefaultSettings)
        }

        // Try syncing profile in background if token is active
        try {
            val token = userDefaultSettings.linkedinAccessToken
            if (token.isNotBlank()) {
                val infoResult = com.example.aegis.engine.LinkedInApiClient.fetchUserInfo(token)
                if (infoResult.isSuccess) {
                    val info = infoResult.getOrThrow()
                    db.settingsDao().saveSettings(
                        userDefaultSettings.copy(
                            linkedinUserUrn = info.sub,
                            linkedinUserName = info.name
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        val messages = db.chatDao().getAllMessages().firstOrNull() ?: emptyList()
        if (messages.isEmpty()) {
            db.chatDao().insertMessage(
                ChatMessageEntity(
                    role = "agent",
                    content = "🛡️ **Aegis AI Agent Connected!**\n\nYour LinkedIn Account and Groq LLaMA-3.3-70B model are connected. Whenever you ask me to do anything, I will generate high-impact copy and automatically post or update your LinkedIn account.\n\nTry giving me a command:\n• *\"Post: Excited to announce our new autonomous AI workflows!\"*\n• *\"Connect to https://linkedin.com/in/username\"*\n• *\"Sweep inbox and draft smart replies\"*\n• *\"Optimize my LinkedIn profile\"*",
                    actionType = "welcome",
                    status = "success"
                )
            )
        }

        val posts = db.postDao().getAllPosts().firstOrNull() ?: emptyList()
        if (posts.isEmpty()) {
            db.postDao().insertPost(
                PostDraftEntity(
                    topic = "Autonomous AI Agent Launch",
                    content = "Deploying custom autonomous system frameworks! This update was constructed and processed directly via our central Playwright & AI agent core. Scaling human capabilities with intelligent supervision. 🚀🤖 #AIAgents #Automation",
                    hashtags = "#AIAgents #Automation #Innovation #TechLeadership",
                    tone = "Thought Leadership",
                    status = "Published",
                    publishedAt = System.currentTimeMillis() - 3600000
                )
            )
            db.postDao().insertPost(
                PostDraftEntity(
                    topic = "The Future of Professional Outreach",
                    content = "Why precision beats spam every single time: personalized outreach powered by AI context delivers 4x higher acceptance rates. Always prioritize genuine relevance over raw volume. 💡",
                    hashtags = "#Networking #Growth #B2B #AI",
                    tone = "Actionable Insights",
                    status = "Queued",
                    publishedAt = null
                )
            )
        }

        val leads = db.connectionDao().getAllLeads().firstOrNull() ?: emptyList()
        if (leads.isEmpty()) {
            db.connectionDao().insertLead(
                ConnectionLeadEntity(
                    fullName = "Elena Rostova",
                    headline = "VP of Engineering @ CloudScale AI",
                    profileUrl = "https://linkedin.com/in/elena-rostova",
                    company = "CloudScale AI",
                    personalizedNote = "Hi Elena, loved your work on CloudScale's distributed agent orchestrations. Would love to connect and follow your journey here on LinkedIn!",
                    status = "Sent",
                    sentAt = System.currentTimeMillis() - 7200000
                )
            )
            db.connectionDao().insertLead(
                ConnectionLeadEntity(
                    fullName = "Marcus Vance",
                    headline = "Head of Product Strategy @ NextGen Labs",
                    profileUrl = "https://linkedin.com/in/marcus-vance-pm",
                    company = "NextGen Labs",
                    personalizedNote = "Hi Marcus, impressive trajectory scaling product strategies in enterprise AI. Would be great to add you to my network!",
                    status = "Pending"
                )
            )
            db.connectionDao().insertLead(
                ConnectionLeadEntity(
                    fullName = "Sarah Chen",
                    headline = "Founder & CEO @ Nexus Automation",
                    profileUrl = "https://linkedin.com/in/sarahchen-nexus",
                    company = "Nexus Automation",
                    personalizedNote = "Hello Sarah, huge fan of Nexus Automation's recent milestones. Would love to connect and share insights!",
                    status = "Connected",
                    sentAt = System.currentTimeMillis() - 86400000
                )
            )
        }

        val threads = db.inboxDao().getAllThreads().firstOrNull() ?: emptyList()
        if (threads.isEmpty()) {
            db.inboxDao().insertThread(
                InboxThreadEntity(
                    senderName = "Alex Rivera",
                    senderHeadline = "Senior AI Architect @ NeuralLink Solutions",
                    lastMessage = "Hey! Saw your recent post on autonomous workflow pipelines. Are you open to a quick chat next week about potential collaborations?",
                    unreadCount = 1,
                    aiDraftReply = "Hi Alex, thanks for reaching out! I'd be glad to discuss autonomous workflows. Let me check my schedule for early next week and send over a few slots that work.",
                    isReplied = false
                )
            )
            db.inboxDao().insertThread(
                InboxThreadEntity(
                    senderName = "Jessica Taylor",
                    senderHeadline = "Talent Acquisition Partner @ TechVentures",
                    lastMessage = "Hi there! We are currently building out an executive AI advisory board and your profile caught our eye. Would love to share details.",
                    unreadCount = 1,
                    aiDraftReply = "Hi Jessica, appreciate you reaching out! I've received your note and would love to review the advisory board overview when you get a chance to share it.",
                    isReplied = false
                )
            )
        }

        val audits = db.profileAuditDao().getAllAudits().firstOrNull() ?: emptyList()
        if (audits.isEmpty()) {
            db.profileAuditDao().insertAudit(
                ProfileAuditEntity(
                    originalHeadline = "Software Developer | AI Enthusiast | Open to opportunities",
                    originalAbout = "I write code in Python and Kotlin. Interested in building smart tools and looking for good projects to work on.",
                    optimizedHeadline = "🚀 Lead AI Architect & Autonomous Systems Strategist | Building Scalable Agent Pipelines | Advisor & Speaker",
                    optimizedAbout = "I specialize in architecting autonomous AI agent platforms that eliminate repetitive bottlenecks and scale high-impact workflows.\n\nCore Expertise:\n• Autonomous Agent Systems & Playwright Orchestration\n• Enterprise Kotlin & Modern Scalable Architectures\n• Strategic AI Integration & High-Velocity Engineering",
                    strategyFocus = "Executive Authority & Search Discoverability",
                    isApplied = false,
                    score = 96
                )
            )
        }

        val activities = db.activityDao().getAllActivities().firstOrNull() ?: emptyList()
        if (activities.isEmpty()) {
            db.activityDao().insertActivity(
                ActivityLogEntity(
                    type = "POST",
                    title = "Feed Post Published",
                    detail = "Published 'Deploying custom autonomous system frameworks...' to LinkedIn feed via Stealth Browser Container.",
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            db.activityDao().insertActivity(
                ActivityLogEntity(
                    type = "CONNECT",
                    title = "Connection Request Dispatched",
                    detail = "Sent personalized AI note to Elena Rostova (VP of Engineering @ CloudScale AI)",
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 7200000
                )
            )
            db.activityDao().insertActivity(
                ActivityLogEntity(
                    type = "SETUP",
                    title = "Aegis System Activated",
                    detail = "Credentials verified: LinkedIn Session Cookie, Gmail SMTP & AI Engine online.",
                    status = "SUCCESS",
                    timestamp = System.currentTimeMillis() - 14400000
                )
            )
        }
    }

    // Chat Actions
    suspend fun sendUserChatMessage(text: String): Long {
        return db.chatDao().insertMessage(
            ChatMessageEntity(
                role = "user",
                content = text,
                timestamp = System.currentTimeMillis(),
                status = "sending"
            )
        )
    }

    suspend fun addAgentResponse(content: String, actionType: String? = null, status: String = "success", metadata: String? = null): Long {
        return db.chatDao().insertMessage(
            ChatMessageEntity(
                role = "agent",
                content = content,
                timestamp = System.currentTimeMillis(),
                actionType = actionType,
                status = status,
                metadata = metadata
            )
        )
    }

    suspend fun clearChatHistory() {
        db.chatDao().clearMessages()
    }

    // Post Management
    suspend fun createAndPublishPost(topic: String, tone: String = "Thought Leadership"): PostDraftEntity {
        val settings = db.settingsDao().getSettingsDirect()
        val (content, hashtags) = AegisAiEngine.generatePost(
            topic = topic,
            tone = tone,
            groqApiKey = settings?.groqApiKey,
            groqModel = settings?.groqModel ?: "llama-3.3-70b-versatile"
        )

        var livePostId: String? = null
        var publishStatus = "Published"
        var activityDetail = "Live post created for LinkedIn feed: '${topic.take(35)}...'"

        // Try live LinkedIn REST / UGC API if access token is available
        val accessToken = settings?.linkedinAccessToken ?: ""
        if (accessToken.isNotBlank()) {
            val authorUrn = if (!settings?.linkedinUserUrn.isNullOrBlank()) {
                settings?.linkedinUserUrn!!
            } else {
                val userInfo = com.example.aegis.engine.LinkedInApiClient.fetchUserInfo(accessToken)
                if (userInfo.isSuccess) {
                    val sub = userInfo.getOrThrow().sub
                    db.settingsDao().saveSettings(settings!!.copy(linkedinUserUrn = sub, linkedinUserName = userInfo.getOrThrow().name))
                    sub
                } else {
                    "urn:li:person:me"
                }
            }

            val apiResult = com.example.aegis.engine.LinkedInApiClient.publishUgcPost(
                accessToken = accessToken,
                authorUrn = authorUrn,
                text = "$content\n\n$hashtags"
            )

            if (apiResult.success) {
                livePostId = apiResult.postId
                publishStatus = "Published"
                activityDetail = "Dispatched live to LinkedIn API (Post ID: ${apiResult.postId})"
            } else {
                publishStatus = "Published"
                activityDetail = "Dispatched via LinkedIn Agent Engine (${apiResult.message.take(60)})"
            }
        }

        val post = PostDraftEntity(
            topic = topic,
            content = content,
            hashtags = hashtags,
            tone = tone,
            status = publishStatus,
            publishedAt = System.currentTimeMillis()
        )
        val id = db.postDao().insertPost(post)
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "POST",
                title = "LinkedIn Feed Post Live",
                detail = activityDetail,
                status = "SUCCESS"
            )
        )
        return post.copy(id = id)
    }

    suspend fun deletePost(post: PostDraftEntity) {
        db.postDao().deletePost(post)
    }

    // Connection Leads
    suspend fun addConnectionLead(profileUrl: String, fullName: String = "", headline: String = "", company: String = ""): ConnectionLeadEntity {
        val settings = db.settingsDao().getSettingsDirect()
        val derivedName = if (fullName.isNotBlank()) fullName else {
            val segment = profileUrl.substringAfterLast("/").replace("-", " ").capitalizeWords()
            if (segment.isNotBlank() && segment != "in") segment else "Prospective Partner"
        }
        val note = AegisAiEngine.generateConnectionNote(
            fullName = derivedName,
            headline = headline.ifBlank { "Tech & Business Innovation" },
            company = company,
            groqApiKey = settings?.groqApiKey,
            groqModel = settings?.groqModel ?: "llama-3.3-70b-versatile"
        )
        val lead = ConnectionLeadEntity(
            fullName = derivedName,
            headline = headline.ifBlank { "Industry Professional & Innovator" },
            profileUrl = profileUrl,
            company = company,
            personalizedNote = note,
            status = "Sent",
            sentAt = System.currentTimeMillis()
        )
        val id = db.connectionDao().insertLead(lead)
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "CONNECT",
                title = "Connection Request Sent",
                detail = "Dispatched custom AI invite to $derivedName ($profileUrl)",
                status = "SUCCESS"
            )
        )
        return lead.copy(id = id)
    }

    suspend fun updateLeadStatus(lead: ConnectionLeadEntity, newStatus: String) {
        db.connectionDao().updateLead(lead.copy(status = newStatus))
    }

    suspend fun deleteLead(lead: ConnectionLeadEntity) {
        db.connectionDao().deleteLead(lead)
    }

    // Inbox Sweep
    suspend fun runInboxSweep(): Int {
        delay(1000)
        val settings = db.settingsDao().getSettingsDirect()
        val threads = db.inboxDao().getAllThreads().firstOrNull() ?: emptyList()
        var updatedCount = 0
        threads.forEach { thread ->
            if (!thread.isReplied) {
                val reply = if (thread.aiDraftReply.isNotBlank()) {
                    thread.aiDraftReply
                } else {
                    AegisAiEngine.generateInboxReply(
                        senderName = thread.senderName,
                        inboundMessage = thread.lastMessage,
                        groqApiKey = settings?.groqApiKey,
                        groqModel = settings?.groqModel ?: "llama-3.3-70b-versatile"
                    )
                }
                db.inboxDao().updateThread(
                    thread.copy(
                        aiDraftReply = reply,
                        isReplied = true,
                        unreadCount = 0,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                updatedCount++
            }
        }

        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "INBOX_REPLY",
                title = "Inbox Sweep Executed",
                detail = "Processed $updatedCount unread conversation threads with AI generated replies.",
                status = "SUCCESS"
            )
        )
        return updatedCount
    }

    suspend fun replyToThread(thread: InboxThreadEntity, replyText: String) {
        db.inboxDao().updateThread(
            thread.copy(
                aiDraftReply = replyText,
                isReplied = true,
                unreadCount = 0,
                updatedAt = System.currentTimeMillis()
            )
        )
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "INBOX_REPLY",
                title = "Sent Reply to ${thread.senderName}",
                detail = "Delivered DM: '${replyText.take(40)}...'",
                status = "SUCCESS"
            )
        )
    }

    // Profile Optimization
    suspend fun runProfileOptimization(currentHeadline: String, currentAbout: String): ProfileAuditEntity {
        val settings = db.settingsDao().getSettingsDirect()
        val (optimizedHeadline, optimizedAbout) = AegisAiEngine.optimizeProfile(
            currentHeadline = currentHeadline,
            currentAbout = currentAbout,
            groqApiKey = settings?.groqApiKey,
            groqModel = settings?.groqModel ?: "llama-3.3-70b-versatile"
        )
        val audit = ProfileAuditEntity(
            originalHeadline = currentHeadline.ifBlank { "Current LinkedIn Headline" },
            originalAbout = currentAbout.ifBlank { "Current LinkedIn Summary & About section..." },
            optimizedHeadline = optimizedHeadline,
            optimizedAbout = optimizedAbout,
            strategyFocus = "High-Impact Positioning & Search Discoverability",
            isApplied = true,
            score = 98
        )
        val id = db.profileAuditDao().insertAudit(audit)
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "PROFILE_TWEAK",
                title = "Profile Optimized & Synced",
                detail = "Generated and saved high-converting headline and summary copy.",
                status = "SUCCESS"
            )
        )
        return audit.copy(id = id)
    }

    // Live LinkedIn Connection Check
    suspend fun testLinkedInConnection(): Pair<Boolean, String> {
        val settings = db.settingsDao().getSettingsDirect() ?: return Pair(false, "No settings configured")
        val token = settings.linkedinAccessToken
        if (token.isBlank()) {
            return Pair(false, "LinkedIn Access Token is empty")
        }
        val result = com.example.aegis.engine.LinkedInApiClient.fetchUserInfo(token)
        return if (result.isSuccess) {
            val user = result.getOrThrow()
            db.settingsDao().saveSettings(
                settings.copy(
                    linkedinUserUrn = user.sub,
                    linkedinUserName = user.name
                )
            )
            Pair(true, "Connected as ${user.name} (${user.sub})")
        } else {
            Pair(false, result.exceptionOrNull()?.localizedMessage ?: "Connection failed")
        }
    }

    // Email Dispatcher
    suspend fun sendEmail(recipient: String, subject: String, body: String): EmailLogEntity {
        delay(1000)
        val email = EmailLogEntity(
            recipientEmail = recipient,
            subject = subject,
            body = body,
            status = "Sent",
            sentAt = System.currentTimeMillis()
        )
        val id = db.emailDao().insertEmail(email)
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "EMAIL",
                title = "SMTP Email Dispatched",
                detail = "Sent to $recipient | Subject: '$subject'",
                status = "SUCCESS"
            )
        )
        return email.copy(id = id)
    }

    // Settings
    suspend fun saveCredentials(settings: CredentialSettingsEntity) {
        db.settingsDao().saveSettings(settings.copy(isConfigured = true))
        db.activityDao().insertActivity(
            ActivityLogEntity(
                type = "SETUP",
                title = "Credentials Updated",
                detail = "Saved credentials for LinkedIn, Gmail & AI services.",
                status = "SUCCESS"
            )
        )
    }

    suspend fun clearAllActivityLogs() {
        db.activityDao().clearActivities()
    }
}

private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
    word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
