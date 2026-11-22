package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.manuscript.BookCatalog
import com.example.data.manuscript.ReaderFontStyle
import com.example.data.manuscript.ReaderSettingsState
import com.example.data.manuscript.ReaderThemeMode
import com.example.data.manuscript.ReadingWidthMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class ReaderRepository(
    private val readerDao: ReaderDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("why_i_changed_prefs", Context.MODE_PRIVATE)

    val bookmarksFlow: Flow<List<BookmarkEntity>> = readerDao.getAllBookmarks()
    val highlightsFlow: Flow<List<HighlightEntity>> = readerDao.getAllHighlights()
    val notesFlow: Flow<List<NoteEntity>> = readerDao.getAllNotes()
    val readingHistoryFlow: Flow<List<ReadingHistoryEntity>> = readerDao.getAllReadingHistory()

    fun loadSettings(): ReaderSettingsState {
        val themeStr = prefs.getString("theme_mode", ReaderThemeMode.DARK.name) ?: ReaderThemeMode.DARK.name
        val widthStr = prefs.getString("width_mode", ReadingWidthMode.BALANCED.name) ?: ReadingWidthMode.BALANCED.name
        val fontStr = prefs.getString("font_style", ReaderFontStyle.SANS.name) ?: ReaderFontStyle.SANS.name
        val fontSize = prefs.getFloat("font_size_sp", 18f).coerceIn(14f, 28f)
        val lineSpacing = prefs.getFloat("line_spacing", 1.75f).coerceIn(1.3f, 2.2f)
        val lastChapterId = prefs.getString("last_chapter_id", "prologue") ?: "prologue"

        return ReaderSettingsState(
            themeMode = runCatching { ReaderThemeMode.valueOf(themeStr) }.getOrDefault(ReaderThemeMode.DARK),
            fontSizeSp = fontSize,
            lineSpacingMultiplier = lineSpacing,
            widthMode = runCatching { ReadingWidthMode.valueOf(widthStr) }.getOrDefault(ReadingWidthMode.BALANCED),
            fontStyle = runCatching { ReaderFontStyle.valueOf(fontStr) }.getOrDefault(ReaderFontStyle.SANS),
            lastReadChapterId = lastChapterId
        )
    }

    fun saveSettings(state: ReaderSettingsState) {
        prefs.edit()
            .putString("theme_mode", state.themeMode.name)
            .putFloat("font_size_sp", state.fontSizeSp)
            .putFloat("line_spacing", state.lineSpacingMultiplier)
            .putString("width_mode", state.widthMode.name)
            .putString("font_style", state.fontStyle.name)
            .putString("last_chapter_id", state.lastReadChapterId)
            .apply()
    }

    suspend fun toggleBookmark(
        chapterId: String,
        badgeLabel: String,
        chapterTitle: String,
        subtitle: String,
        isCurrentlyBookmarked: Boolean
    ) {
        if (isCurrentlyBookmarked) {
            readerDao.deleteBookmark(chapterId)
        } else {
            readerDao.insertBookmark(
                BookmarkEntity(
                    chapterId = chapterId,
                    badgeLabel = badgeLabel,
                    chapterTitle = chapterTitle,
                    subtitle = subtitle
                )
            )
        }
    }

    suspend fun removeBookmark(chapterId: String) {
        readerDao.deleteBookmark(chapterId)
    }

    suspend fun addOrUpdateHighlight(
        chapterId: String,
        badgeLabel: String,
        chapterTitle: String,
        paragraphIndex: Int,
        text: String,
        colorName: String
    ) {
        readerDao.deleteHighlightForParagraph(chapterId, paragraphIndex)
        readerDao.insertHighlight(
            HighlightEntity(
                chapterId = chapterId,
                badgeLabel = badgeLabel,
                chapterTitle = chapterTitle,
                paragraphIndex = paragraphIndex,
                highlightedText = text,
                colorName = colorName
            )
        )
    }

    suspend fun removeHighlightForParagraph(chapterId: String, paragraphIndex: Int) {
        readerDao.deleteHighlightForParagraph(chapterId, paragraphIndex)
    }

    suspend fun removeHighlightById(id: Int) {
        readerDao.deleteHighlightById(id)
    }

    suspend fun saveNote(
        id: Int = 0,
        chapterId: String,
        badgeLabel: String,
        chapterTitle: String,
        paragraphIndex: Int?,
        selectedPassage: String,
        noteText: String
    ) {
        readerDao.insertNote(
            NoteEntity(
                id = id,
                chapterId = chapterId,
                badgeLabel = badgeLabel,
                chapterTitle = chapterTitle,
                paragraphIndex = paragraphIndex,
                selectedPassage = selectedPassage,
                noteText = noteText
            )
        )
    }

    suspend fun removeNoteById(id: Int) {
        readerDao.deleteNoteById(id)
    }

    suspend fun recordChapterVisit(
        chapterId: String,
        badgeLabel: String,
        chapterTitle: String,
        progressPercent: Int,
        isCompleted: Boolean
    ) {
        readerDao.upsertReadingHistory(
            ReadingHistoryEntity(
                chapterId = chapterId,
                badgeLabel = badgeLabel,
                chapterTitle = chapterTitle,
                progressPercent = progressPercent.coerceIn(0, 100),
                isCompleted = isCompleted,
                lastReadTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeHistoryItem(chapterId: String) {
        readerDao.deleteReadingHistory(chapterId)
    }

    suspend fun resetAllReadingData() {
        readerDao.clearAllBookmarks()
        readerDao.clearAllHighlights()
        readerDao.clearAllNotes()
        readerDao.clearAllReadingHistory()
        saveSettings(ReaderSettingsState())
    }

    suspend fun exportReadingDataToJson(currentSettings: ReaderSettingsState): String {
        val bookmarks = bookmarksFlow.first()
        val highlights = highlightsFlow.first()
        val notes = notesFlow.first()
        val history = readingHistoryFlow.first()

        val root = JSONObject()
        root.put("bookTitle", BookCatalog.metadata.title)
        root.put("bookSubtitle", BookCatalog.metadata.subtitle)
        root.put("author", BookCatalog.metadata.author)
        root.put("exportedAt", System.currentTimeMillis())

        val settingsObj = JSONObject().apply {
            put("themeMode", currentSettings.themeMode.name)
            put("fontSizeSp", currentSettings.fontSizeSp.toDouble())
            put("lineSpacingMultiplier", currentSettings.lineSpacingMultiplier.toDouble())
            put("widthMode", currentSettings.widthMode.name)
            put("fontStyle", currentSettings.fontStyle.name)
            put("lastReadChapterId", currentSettings.lastReadChapterId)
        }
        root.put("settings", settingsObj)

        val bookmarksArr = JSONArray()
        bookmarks.forEach { b ->
            bookmarksArr.put(
                JSONObject().apply {
                    put("chapterId", b.chapterId)
                    put("badgeLabel", b.badgeLabel)
                    put("chapterTitle", b.chapterTitle)
                    put("subtitle", b.subtitle)
                    put("timestamp", b.timestamp)
                }
            )
        }
        root.put("bookmarks", bookmarksArr)

        val highlightsArr = JSONArray()
        highlights.forEach { h ->
            highlightsArr.put(
                JSONObject().apply {
                    put("chapterId", h.chapterId)
                    put("badgeLabel", h.badgeLabel)
                    put("chapterTitle", h.chapterTitle)
                    put("paragraphIndex", h.paragraphIndex)
                    put("highlightedText", h.highlightedText)
                    put("colorName", h.colorName)
                    put("timestamp", h.timestamp)
                }
            )
        }
        root.put("highlights", highlightsArr)

        val notesArr = JSONArray()
        notes.forEach { n ->
            notesArr.put(
                JSONObject().apply {
                    put("chapterId", n.chapterId)
                    put("badgeLabel", n.badgeLabel)
                    put("chapterTitle", n.chapterTitle)
                    if (n.paragraphIndex != null) put("paragraphIndex", n.paragraphIndex)
                    put("selectedPassage", n.selectedPassage)
                    put("noteText", n.noteText)
                    put("timestamp", n.timestamp)
                }
            )
        }
        root.put("notes", notesArr)

        val historyArr = JSONArray()
        history.forEach { r ->
            historyArr.put(
                JSONObject().apply {
                    put("chapterId", r.chapterId)
                    put("badgeLabel", r.badgeLabel)
                    put("chapterTitle", r.chapterTitle)
                    put("progressPercent", r.progressPercent)
                    put("isCompleted", r.isCompleted)
                    put("lastReadTimestamp", r.lastReadTimestamp)
                }
            )
        }
        root.put("readingHistory", historyArr)

        return root.toString(2)
    }

    suspend fun importReadingDataFromJson(jsonString: String): Result<ReaderSettingsState> {
        return runCatching {
            val root = JSONObject(jsonString)

            val settingsObj = root.optJSONObject("settings")
            val importedSettings = if (settingsObj != null) {
                ReaderSettingsState(
                    themeMode = runCatching {
                        ReaderThemeMode.valueOf(settingsObj.optString("themeMode", "DARK"))
                    }.getOrDefault(ReaderThemeMode.DARK),
                    fontSizeSp = settingsObj.optDouble("fontSizeSp", 18.0).toFloat().coerceIn(14f, 28f),
                    lineSpacingMultiplier = settingsObj.optDouble("lineSpacingMultiplier", 1.75).toFloat().coerceIn(1.3f, 2.2f),
                    widthMode = runCatching {
                        ReadingWidthMode.valueOf(settingsObj.optString("widthMode", "BALANCED"))
                    }.getOrDefault(ReadingWidthMode.BALANCED),
                    fontStyle = runCatching {
                        ReaderFontStyle.valueOf(settingsObj.optString("fontStyle", "SANS"))
                    }.getOrDefault(ReaderFontStyle.SANS),
                    lastReadChapterId = settingsObj.optString("lastReadChapterId", "prologue")
                )
            } else {
                loadSettings()
            }
            saveSettings(importedSettings)

            val bookmarksArr = root.optJSONArray("bookmarks")
            if (bookmarksArr != null) {
                for (i in 0 until bookmarksArr.length()) {
                    val obj = bookmarksArr.getJSONObject(i)
                    readerDao.insertBookmark(
                        BookmarkEntity(
                            chapterId = obj.getString("chapterId"),
                            badgeLabel = obj.optString("badgeLabel", ""),
                            chapterTitle = obj.optString("chapterTitle", ""),
                            subtitle = obj.optString("subtitle", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            val highlightsArr = root.optJSONArray("highlights")
            if (highlightsArr != null) {
                for (i in 0 until highlightsArr.length()) {
                    val obj = highlightsArr.getJSONObject(i)
                    readerDao.insertHighlight(
                        HighlightEntity(
                            chapterId = obj.getString("chapterId"),
                            badgeLabel = obj.optString("badgeLabel", ""),
                            chapterTitle = obj.optString("chapterTitle", ""),
                            paragraphIndex = obj.optInt("paragraphIndex", 0),
                            highlightedText = obj.optString("highlightedText", ""),
                            colorName = obj.optString("colorName", "GOLD"),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            val notesArr = root.optJSONArray("notes")
            if (notesArr != null) {
                for (i in 0 until notesArr.length()) {
                    val obj = notesArr.getJSONObject(i)
                    readerDao.insertNote(
                        NoteEntity(
                            chapterId = obj.getString("chapterId"),
                            badgeLabel = obj.optString("badgeLabel", ""),
                            chapterTitle = obj.optString("chapterTitle", ""),
                            paragraphIndex = if (obj.has("paragraphIndex")) obj.optInt("paragraphIndex") else null,
                            selectedPassage = obj.optString("selectedPassage", ""),
                            noteText = obj.optString("noteText", ""),
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            val historyArr = root.optJSONArray("readingHistory")
            if (historyArr != null) {
                for (i in 0 until historyArr.length()) {
                    val obj = historyArr.getJSONObject(i)
                    readerDao.upsertReadingHistory(
                        ReadingHistoryEntity(
                            chapterId = obj.getString("chapterId"),
                            badgeLabel = obj.optString("badgeLabel", ""),
                            chapterTitle = obj.optString("chapterTitle", ""),
                            progressPercent = obj.optInt("progressPercent", 100),
                            isCompleted = obj.optBoolean("isCompleted", true),
                            lastReadTimestamp = obj.optLong("lastReadTimestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            importedSettings
        }
    }

    fun exportCompleteBookStructureJson(): String {
        val root = JSONObject()
        val bookObj = JSONObject().apply {
            put("title", BookCatalog.metadata.title)
            put("subtitle", BookCatalog.metadata.subtitle)
            put("author", BookCatalog.metadata.author)
            put("language", BookCatalog.metadata.language)
            put("totalParts", 8)
            put("totalChapters", 40)
            put("totalSections", BookCatalog.allChapters.size)
        }
        root.put("book", bookObj)

        val partsArray = JSONArray()
        BookCatalog.parts.forEach { part ->
            partsArray.put(
                JSONObject().apply {
                    put("id", part.id)
                    put("romanNumeral", part.romanNumeral)
                    put("title", part.titleNepali)
                    put("themeSummary", part.themeSummary)
                }
            )
        }
        root.put("parts", partsArray)

        val chaptersArray = JSONArray()
        BookCatalog.allChapters.forEach { ch ->
            chaptersArray.put(
                JSONObject().apply {
                    put("id", ch.id)
                    put("part", ch.partId)
                    put("badgeLabel", ch.badgeLabel)
                    put("title", ch.title)
                    put("introduction", ch.introduction)
                    put("paragraphCount", ch.paragraphs.size)
                    put("memorableQuote", ch.memorableQuote)
                    put("readingTime", "${ch.readingTimeMinutes} min")
                }
            )
        }
        root.put("chapters", chaptersArray)

        return root.toString(2)
    }
}
