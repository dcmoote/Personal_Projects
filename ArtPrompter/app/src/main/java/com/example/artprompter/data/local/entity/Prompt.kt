package com.dcmoote.inkwell.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompts")
data class Prompt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,        // "WRITING" or "ART"
    val genre: String,       // Writing: genre name | Art: medium name
    val subject: String?,    // Art only: "PEOPLE", "LANDSCAPES", "BOTH" — null for writing
    val content: String,
    val source: String,      // "AI" or "LOCAL"
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
