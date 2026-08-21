package com.example.aegis.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aegis.data.dao.*
import com.example.aegis.data.model.*

@Database(
    entities = [
        ChatMessageEntity::class,
        PostDraftEntity::class,
        ConnectionLeadEntity::class,
        InboxThreadEntity::class,
        ProfileAuditEntity::class,
        EmailLogEntity::class,
        ActivityLogEntity::class,
        CredentialSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AegisDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun postDao(): PostDao
    abstract fun connectionDao(): ConnectionDao
    abstract fun inboxDao(): InboxDao
    abstract fun profileAuditDao(): ProfileAuditDao
    abstract fun emailDao(): EmailDao
    abstract fun activityDao(): ActivityDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AegisDatabase? = null

        fun getInstance(context: Context): AegisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AegisDatabase::class.java,
                    "aegis_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
