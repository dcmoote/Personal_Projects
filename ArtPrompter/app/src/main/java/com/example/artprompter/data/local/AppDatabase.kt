package com.dcmoote.inkwell.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dcmoote.inkwell.data.local.dao.PromptDao
import com.dcmoote.inkwell.data.local.entity.Prompt

// Room database definition. Increment version and add a Migration whenever the schema changes.
@Database(entities = [Prompt::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun promptDao(): PromptDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        // Added isCompleted and completionNote columns for the habit tracker feature.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE prompts ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE prompts ADD COLUMN completionNote TEXT")
            }
        }

        // Singleton — one database instance shared across the whole app.
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "art_prompter.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { instance = it }
            }
        }
    }
}
