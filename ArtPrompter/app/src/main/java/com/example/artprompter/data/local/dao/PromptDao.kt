package com.example.artprompter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.artprompter.data.local.entity.Prompt
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {

    @Insert
    suspend fun insert(prompt: Prompt): Long

    @Query("SELECT * FROM prompts ORDER BY timestamp DESC")
    fun getAllPrompts(): Flow<List<Prompt>>

    @Query("SELECT * FROM prompts WHERE type = :type ORDER BY timestamp DESC")
    fun getPromptsByType(type: String): Flow<List<Prompt>>

    @Query("SELECT * FROM prompts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestPrompt(): Prompt?

    @Update
    suspend fun update(prompt: Prompt)

    @Query("DELETE FROM prompts")
    suspend fun deleteAll()
}
