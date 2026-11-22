package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReaderDao {

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE chapterId = :chapterId")
    suspend fun deleteBookmark(chapterId: String)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAllBookmarks()

    // Highlights
    @Query("SELECT * FROM highlights ORDER BY timestamp DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity)

    @Query("DELETE FROM highlights WHERE id = :id")
    suspend fun deleteHighlightById(id: Int)

    @Query("DELETE FROM highlights WHERE chapterId = :chapterId AND paragraphIndex = :paragraphIndex")
    suspend fun deleteHighlightForParagraph(chapterId: String, paragraphIndex: Int)

    @Query("DELETE FROM highlights")
    suspend fun clearAllHighlights()

    // Notes
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    // Reading History & Progress
    @Query("SELECT * FROM reading_history ORDER BY lastReadTimestamp DESC")
    fun getAllReadingHistory(): Flow<List<ReadingHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReadingHistory(history: ReadingHistoryEntity)

    @Query("DELETE FROM reading_history WHERE chapterId = :chapterId")
    suspend fun deleteReadingHistory(chapterId: String)

    @Query("DELETE FROM reading_history")
    suspend fun clearAllReadingHistory()
}
