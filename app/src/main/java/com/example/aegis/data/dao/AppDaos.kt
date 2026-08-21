package com.example.aegis.data.dao

import androidx.room.*
import com.example.aegis.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearMessages()
}

@Dao
interface PostDao {
    @Query("SELECT * FROM post_drafts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostDraftEntity): Long

    @Update
    suspend fun updatePost(post: PostDraftEntity)

    @Delete
    suspend fun deletePost(post: PostDraftEntity)
}

@Dao
interface ConnectionDao {
    @Query("SELECT * FROM connection_leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<ConnectionLeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: ConnectionLeadEntity): Long

    @Update
    suspend fun updateLead(lead: ConnectionLeadEntity)

    @Delete
    suspend fun deleteLead(lead: ConnectionLeadEntity)
}

@Dao
interface InboxDao {
    @Query("SELECT * FROM inbox_threads ORDER BY updatedAt DESC")
    fun getAllThreads(): Flow<List<InboxThreadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: InboxThreadEntity): Long

    @Update
    suspend fun updateThread(thread: InboxThreadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreads(threads: List<InboxThreadEntity>)
}

@Dao
interface ProfileAuditDao {
    @Query("SELECT * FROM profile_audits ORDER BY createdAt DESC")
    fun getAllAudits(): Flow<List<ProfileAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudit(audit: ProfileAuditEntity): Long

    @Update
    suspend fun updateAudit(audit: ProfileAuditEntity)
}

@Dao
interface EmailDao {
    @Query("SELECT * FROM email_logs ORDER BY sentAt DESC")
    fun getAllEmails(): Flow<List<EmailLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailLogEntity): Long
}

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLogEntity): Long

    @Query("DELETE FROM activity_logs")
    suspend fun clearActivities()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM credential_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<CredentialSettingsEntity?>

    @Query("SELECT * FROM credential_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): CredentialSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: CredentialSettingsEntity)
}
