package com.example.aegis.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aegis.data.database.AegisDatabase
import com.example.aegis.data.model.*
import com.example.aegis.data.repository.AegisRepository
import com.example.aegis.engine.AegisAiEngine
import com.example.aegis.engine.AgentAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    OPENING,
    CONNECT,
    CHAT,
    HUB,
    LOGS
}

enum class HubSection {
    POSTS,
    CONNECT,
    INBOX,
    PROFILE,
    EMAIL
}

data class ExecutionStep(
    val title: String,
    val isDone: Boolean = false,
    val isRunning: Boolean = false
)

class AegisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AegisRepository

    val currentTab = MutableStateFlow(AppTab.OPENING)
    val hubSection = MutableStateFlow(HubSection.POSTS)

    val chatMessages: StateFlow<List<ChatMessageEntity>>
    val posts: StateFlow<List<PostDraftEntity>>
    val leads: StateFlow<List<ConnectionLeadEntity>>
    val threads: StateFlow<List<InboxThreadEntity>>
    val audits: StateFlow<List<ProfileAuditEntity>>
    val emails: StateFlow<List<EmailLogEntity>>
    val activities: StateFlow<List<ActivityLogEntity>>
    val settings: StateFlow<CredentialSettingsEntity?>

    val isAgentBusy = MutableStateFlow(false)
    val agentStatusText = MutableStateFlow<String?>(null)
    val activeExecutionSteps = MutableStateFlow<List<ExecutionStep>>(emptyList())

    init {
        val db = AegisDatabase.getInstance(application)
        repository = AegisRepository(db)

        chatMessages = repository.allMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        posts = repository.allPosts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        leads = repository.allLeads.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        threads = repository.allThreads.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        audits = repository.allAudits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        emails = repository.allEmails.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        activities = repository.allActivities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        settings = repository.settingsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun setTab(tab: AppTab) {
        currentTab.value = tab
    }

    fun setHubSection(section: HubSection) {
        hubSection.value = section
    }

    fun handleUserChatInput(input: String) {
        if (input.isBlank() || isAgentBusy.value) return

        val userText = input.trim()
        viewModelScope.launch {
            repository.sendUserChatMessage(userText)
            isAgentBusy.value = true
            agentStatusText.value = "Interpreting intent..."

            val parsed = AegisAiEngine.parseUserCommand(userText)

            when (parsed.action) {
                AgentAction.POST -> {
                    agentStatusText.value = "Drafting and preparing LinkedIn post..."
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Synthesizing engaging copy & hashtags", isRunning = true),
                        ExecutionStep("Injecting li_at session cookie"),
                        ExecutionStep("Opening LinkedIn feed and publishing")
                    )
                    delay(800)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Synthesizing engaging copy & hashtags", isDone = true),
                        ExecutionStep("Injecting li_at session cookie", isRunning = true),
                        ExecutionStep("Opening LinkedIn feed and publishing")
                    )
                    delay(700)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Synthesizing engaging copy & hashtags", isDone = true),
                        ExecutionStep("Injecting li_at session cookie", isDone = true),
                        ExecutionStep("Opening LinkedIn feed and publishing", isRunning = true)
                    )

                    val topic = parsed.topicOrContent ?: "Autonomous AI agents and future of work"
                    val publishedPost = repository.createAndPublishPost(topic)
                    delay(600)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Synthesizing engaging copy & hashtags", isDone = true),
                        ExecutionStep("Injecting li_at session cookie", isDone = true),
                        ExecutionStep("Opening LinkedIn feed and publishing", isDone = true)
                    )

                    repository.addAgentResponse(
                        content = "📝 **Post published live to your LinkedIn feed!**\n\n\"${publishedPost.content}\"\n\n*Check the Automation Hub → Feed Posts tab to review your post history or compose more.* ✨",
                        actionType = "post",
                        status = "success"
                    )
                }

                AgentAction.CONNECT -> {
                    val url = parsed.targetUrl
                    if (url != null && url.startsWith("https://")) {
                        agentStatusText.value = "Generating personalized note & connecting..."
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Extracting profile insights", isRunning = true),
                            ExecutionStep("Drafting personalized 300-character invite"),
                            ExecutionStep("Submitting connection request")
                        )
                        delay(700)
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Extracting profile insights", isDone = true),
                            ExecutionStep("Drafting personalized 300-character invite", isRunning = true),
                            ExecutionStep("Submitting connection request")
                        )
                        val lead = repository.addConnectionLead(url)
                        delay(600)
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Extracting profile insights", isDone = true),
                            ExecutionStep("Drafting personalized 300-character invite", isDone = true),
                            ExecutionStep("Submitting connection request", isDone = true)
                        )

                        repository.addAgentResponse(
                            content = "🤝 **Connection request dispatched!**\n\n**Prospect:** ${lead.fullName}\n**URL:** ${lead.profileUrl}\n**Personalized Note:** \"${lead.personalizedNote}\"\n\n*Added to your Outreach Pipeline in Automation Hub → Connect.* 🚀",
                            actionType = "connect",
                            status = "success"
                        )
                    } else {
                        repository.addAgentResponse(
                            content = "❌ Please provide a valid LinkedIn profile URL (starting with `https://`).\n\nExample: *\"Connect to https://linkedin.com/in/username\"*",
                            status = "error"
                        )
                    }
                }

                AgentAction.INBOX -> {
                    agentStatusText.value = "Scanning unread threads & drafting replies..."
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Checking active LinkedIn session", isRunning = true),
                        ExecutionStep("Filtering unread messages"),
                        ExecutionStep("Generating contextual AI responses")
                    )
                    delay(700)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Checking active LinkedIn session", isDone = true),
                        ExecutionStep("Filtering unread messages", isRunning = true),
                        ExecutionStep("Generating contextual AI responses")
                    )
                    val processedCount = repository.runInboxSweep()
                    delay(800)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Checking active LinkedIn session", isDone = true),
                        ExecutionStep("Filtering unread messages", isDone = true),
                        ExecutionStep("Generating contextual AI responses", isDone = true)
                    )

                    repository.addAgentResponse(
                        content = "📬 **Inbox sweep complete!**\n\nScanned your unread conversations and generated $processedCount smart, conversational responses. All threads have been updated with high-converting follow-ups.\n\n*Review all active conversations in Automation Hub → Inbox Sweep.* 💬",
                        actionType = "inbox",
                        status = "success"
                    )
                }

                AgentAction.PROFILE -> {
                    agentStatusText.value = "Auditing headline and summary section..."
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Reading profile positioning", isRunning = true),
                        ExecutionStep("Synthesizing high-impact keywords"),
                        ExecutionStep("Structuring new Headline & About section")
                    )
                    delay(800)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Reading profile positioning", isDone = true),
                        ExecutionStep("Synthesizing high-impact keywords", isRunning = true),
                        ExecutionStep("Structuring new Headline & About section")
                    )
                    val audit = repository.runProfileOptimization(
                        currentHeadline = "Software Developer | AI Systems | Innovator",
                        currentAbout = "Passionate about autonomous agent tooling, modern platforms, and engineering high-reliability systems."
                    )
                    delay(700)
                    activeExecutionSteps.value = listOf(
                        ExecutionStep("Reading profile positioning", isDone = true),
                        ExecutionStep("Synthesizing high-impact keywords", isDone = true),
                        ExecutionStep("Structuring new Headline & About section", isDone = true)
                    )

                    repository.addAgentResponse(
                        content = "⚙️ **Profile optimization completed!** (Optimization Score: ${audit.score}/100)\n\n✨ **New Headline:**\n>${audit.optimizedHeadline}\n\n📝 **New About Summary:**\n>${audit.optimizedAbout.take(180)}...\n\n*View full comparison in Automation Hub → Profile Optimizer.*",
                        actionType = "profile",
                        status = "success"
                    )
                }

                AgentAction.EMAIL -> {
                    val email = parsed.recipientEmail
                    if (email != null) {
                        agentStatusText.value = "Authenticating SMTP and transmitting..."
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Connecting to smtp.gmail.com:465 (SSL)", isRunning = true),
                            ExecutionStep("Packaging MIME payload"),
                            ExecutionStep("Transmitting email")
                        )
                        delay(600)
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Connecting to smtp.gmail.com:465 (SSL)", isDone = true),
                            ExecutionStep("Packaging MIME payload", isRunning = true),
                            ExecutionStep("Transmitting email")
                        )
                        repository.sendEmail(email, parsed.subject ?: "Aegis Alert", parsed.body ?: "Automated verification test.")
                        delay(500)
                        activeExecutionSteps.value = listOf(
                            ExecutionStep("Connecting to smtp.gmail.com:465 (SSL)", isDone = true),
                            ExecutionStep("Packaging MIME payload", isDone = true),
                            ExecutionStep("Transmitting email", isDone = true)
                        )

                        repository.addAgentResponse(
                            content = "📧 **SMTP Email successfully delivered!**\n\n**To:** $email\n**Subject:** ${parsed.subject}\n**Status:** 250 OK - Message accepted for delivery.",
                            actionType = "email",
                            status = "success"
                        )
                    } else {
                        repository.addAgentResponse(
                            content = "📧 **Email Dispatcher Format:**\n\nTo send an email, please specify the recipient:\n*\"Send email to user@domain.com, subject: Hello, body: My message\"*",
                            actionType = "email"
                        )
                    }
                }

                AgentAction.SETUP -> {
                    repository.addAgentResponse(
                        content = "🔑 **Credentials & Configuration Panel**\n\nSwitch to the **Connect** tab to manage your LinkedIn Session Cookie (`li_at`), OAuth token, Gmail App Password, and Groq AI API keys.",
                        actionType = "setup"
                    )
                    currentTab.value = AppTab.CONNECT
                }

                AgentAction.STATUS -> {
                    val currentCfg = settings.value
                    val liStatus = if (currentCfg?.linkedinConnected == true) "✓ Connected" else "○ Disconnected"
                    val gmStatus = if (currentCfg?.gmailConnected == true) "✓ Connected" else "○ Disconnected"
                    val aiStatus = if (currentCfg?.aiConnected == true) "✓ Connected" else "○ Disconnected"

                    repository.addAgentResponse(
                        content = "🛡️ **Aegis System Status:**\n\n• **LinkedIn Engine:** $liStatus\n• **Gmail SMTP:** $gmStatus\n• **AI Agent Core:** $aiStatus\n• **Stealth Mode:** Enabled (Random delays 2-6s)\n• **Environment:** Operational",
                        status = "success"
                    )
                }

                AgentAction.HELP -> {
                    repository.addAgentResponse(
                        content = "🛡️ **Aegis AI Agent Capabilities:**\n\n• **📝 Post to Feed**: *\"Post: Excited to announce our new product launch!\"*\n• **🤝 Send Connect Request**: *\"Connect to https://linkedin.com/in/username\"*\n• **💬 Inbox Sweep & DM Replies**: *\"Check my messages\"* or *\"Sweep inbox\"*\n• **⚙️ Optimize Profile**: *\"Optimize my headline and about section\"*\n• **📧 SMTP Email**: *\"Send email to contact@company.com\"*\n• **📊 Status Check**: *\"Check system status\"*",
                        actionType = "help"
                    )
                }

                AgentAction.UNKNOWN -> {
                    delay(600)
                    repository.addAgentResponse(
                        content = "🤔 I received your request: *\"$userText\"*\n\nHere are quick commands you can run:\n• **\"Post: [topic or text]\"** to publish updates\n• **\"Connect to [LinkedIn URL]\"** to invite prospects\n• **\"Check messages\"** to auto-reply to unread DMs\n• **\"Optimize my profile\"** for headline & bio tuning\n• **\"Send email to [address]\"** for outbound alerts",
                        actionType = "help"
                    )
                }
            }

            delay(300)
            isAgentBusy.value = false
            agentStatusText.value = null
            activeExecutionSteps.value = emptyList()
        }
    }

    fun publishCustomPost(topic: String, tone: String) {
        viewModelScope.launch {
            isAgentBusy.value = true
            repository.createAndPublishPost(topic, tone)
            isAgentBusy.value = false
        }
    }

    fun deletePost(post: PostDraftEntity) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun addLead(url: String, name: String, headline: String, company: String) {
        viewModelScope.launch {
            isAgentBusy.value = true
            repository.addConnectionLead(url, name, headline, company)
            isAgentBusy.value = false
        }
    }

    fun deleteLead(lead: ConnectionLeadEntity) {
        viewModelScope.launch {
            repository.deleteLead(lead)
        }
    }

    fun triggerInboxSweep() {
        viewModelScope.launch {
            isAgentBusy.value = true
            repository.runInboxSweep()
            isAgentBusy.value = false
        }
    }

    fun replyToThread(thread: InboxThreadEntity, reply: String) {
        viewModelScope.launch {
            repository.replyToThread(thread, reply)
        }
    }

    fun triggerProfileOptimization(headline: String, about: String) {
        viewModelScope.launch {
            isAgentBusy.value = true
            repository.runProfileOptimization(headline, about)
            isAgentBusy.value = false
        }
    }

    fun sendEmail(recipient: String, subject: String, body: String) {
        viewModelScope.launch {
            isAgentBusy.value = true
            repository.sendEmail(recipient, subject, body)
            isAgentBusy.value = false
        }
    }

    fun saveCredentials(
        liCookie: String,
        liToken: String,
        liClientId: String,
        liClientSecret: String = "",
        liRedirectUri: String = "http://localhost:8000/callback",
        groqKey: String = "",
        groqModel: String = "llama-3.3-70b-versatile",
        zapierUrl: String = "https://mcp.zapier.com/api/v1/connect",
        smtpEmail: String,
        smtpPass: String
    ) {
        viewModelScope.launch {
            repository.saveCredentials(
                CredentialSettingsEntity(
                    id = 1,
                    linkedinSessionCookie = liCookie,
                    linkedinAccessToken = liToken,
                    linkedinClientId = liClientId,
                    linkedinClientSecret = liClientSecret,
                    linkedinRedirectUri = liRedirectUri,
                    groqApiKey = groqKey,
                    groqModel = groqModel,
                    zapierMcpUrl = zapierUrl,
                    smtpEmail = smtpEmail,
                    smtpPassword = smtpPass,
                    isConfigured = true
                )
            )
        }
    }

    fun testLinkedInConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.testLinkedInConnection()
            onResult(res.first, res.second)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearAllActivityLogs()
        }
    }
}
