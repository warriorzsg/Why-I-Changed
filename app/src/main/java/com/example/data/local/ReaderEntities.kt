package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val chapterId: String,
    val badgeLabel: String,
    val chapterTitle: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: String,
    val badgeLabel: String,
    val chapterTitle: String,
    val paragraphIndex: Int,
    val highlightedText: String,
    val colorName: String = "GOLD", // GOLD, AMBER, SAGE, ROSE
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: String,
    val badgeLabel: String,
    val chapterTitle: String,
    val paragraphIndex: Int?, // null if general chapter note
    val selectedPassage: String,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val chapterId: String,
    val badgeLabel: String,
    val chapterTitle: String,
    val progressPercent: Int, // 0..100
    val isCompleted: Boolean,
    val lastReadTimestamp: Long = System.currentTimeMillis()
)
