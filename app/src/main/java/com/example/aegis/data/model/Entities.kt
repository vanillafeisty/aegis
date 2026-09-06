package com.example.aegis.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "chat_messages")
@Serializable
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "agent"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String? = null, // "post", "connect", "inbox", "profile", "email", "setup"
    val status: String? = null, // "sending", "success", "error", "executing"
    val metadata: String? = null
)

@Entity(tableName = "post_drafts")
@Serializable
data class PostDraftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val content: String,
    val hashtags: String = "",
    val tone: String = "Thought Leadership",
    val status: String = "Draft", // "Draft", "Queued", "Published", "Failed"
    val createdAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = null
)

@Entity(tableName = "connection_leads")
@Serializable
data class ConnectionLeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val headline: String,
    val profileUrl: String,
    val company: String = "",
    val personalizedNote: String = "",
    val status: String = "Pending", // "Pending", "Queued", "Sent", "Connected"
    val createdAt: Long = System.currentTimeMillis(),
    val sentAt: Long? = null
)

@Entity(tableName = "inbox_threads")
@Serializable
data class InboxThreadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val senderHeadline: String,
    val lastMessage: String,
    val unreadCount: Int = 1,
    val aiDraftReply: String = "",
    val isReplied: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "profile_audits")
@Serializable
data class ProfileAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalHeadline: String,
    val originalAbout: String,
    val optimizedHeadline: String,
    val optimizedAbout: String,
    val strategyFocus: String = "High Authority & Search Visibility",
    val isApplied: Boolean = false,
    val score: Int = 94,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "email_logs")
@Serializable
data class EmailLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientEmail: String,
    val subject: String,
    val body: String,
    val status: String = "Sent", // "Sent", "Failed", "Queued"
    val sentAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "activity_logs")
@Serializable
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "POST", "CONNECT", "INBOX_REPLY", "PROFILE_TWEAK", "EMAIL", "SETUP"
    val title: String,
    val detail: String,
    val status: String = "SUCCESS", // "SUCCESS", "RUNNING", "PENDING", "ERROR"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "credential_settings")
@Serializable
data class CredentialSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val linkedinSessionCookie: String = "",
    val linkedinAccessToken: String = "",
    val linkedinClientId: String = "",
    val linkedinClientSecret: String = "",
    val linkedinRedirectUri: String = "http://localhost:8000/callback",
    val groqApiKey: String = "",
    val groqModel: String = "llama-3.3-70b-versatile",
    val zapierMcpUrl: String = "https://mcp.zapier.com/api/v1/connect",
    val openaiApiKey: String = "",
    val smtpEmail: String = "",
    val smtpPassword: String = "",
    val environment: String = "development",
    val debugMode: Boolean = true,
    val linkedinUserUrn: String = "",
    val linkedinUserName: String = "",
    val isConfigured: Boolean = true,
    val stealthModeEnabled: Boolean = true,
    val humanTypingCadence: Boolean = true
) {
    val linkedinConnected: Boolean get() = linkedinAccessToken.isNotBlank() || linkedinSessionCookie.isNotBlank()
    val gmailConnected: Boolean get() = smtpEmail.isNotBlank() && smtpPassword.isNotBlank()
    val aiConnected: Boolean get() = groqApiKey.isNotBlank() || openaiApiKey.isNotBlank()
    val zapierConnected: Boolean get() = zapierMcpUrl.isNotBlank()
    val allConfigured: Boolean get() = linkedinConnected && gmailConnected && aiConnected
}
