package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.HighlightEntity
import com.example.data.local.NoteEntity
import com.example.data.local.ReaderDatabase
import com.example.data.local.ReaderRepository
import com.example.data.local.ReadingHistoryEntity
import com.example.data.manuscript.BookCatalog
import com.example.data.manuscript.BookChapter
import com.example.data.manuscript.ReaderFontStyle
import com.example.data.manuscript.ReaderSettingsState
import com.example.data.manuscript.ReaderThemeMode
import com.example.data.manuscript.ReadingWidthMode
import com.example.data.manuscript.SearchResultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppNavTab(val routeId: String, val labelEnglish: String, val labelNepali: String) {
    HOME("home", "Home", "गृहपृष्ठ"),
    READ("read", "Read", "अध्ययन"),
    SAVED("saved", "Saved", "संग्रह"),
    SETTINGS("settings", "Settings", "सेटिङ्स")
}

enum class SavedSubTab(val labelNepali: String) {
    BOOKMARKS("बुकमार्क"),
    HIGHLIGHTS("हाइलाइट"),
    NOTES("टिप्पणी (Notes)"),
    HISTORY("अध्ययन इतिहास")
}

class BookReaderViewModel(
    private val repository: ReaderRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _savedSubTab = MutableStateFlow(SavedSubTab.BOOKMARKS)
    val savedSubTab: StateFlow<SavedSubTab> = _savedSubTab.asStateFlow()

    private val _settings = MutableStateFlow(repository.loadSettings())
    val settings: StateFlow<ReaderSettingsState> = _settings.asStateFlow()

    private val _currentChapter = MutableStateFlow(
        BookCatalog.getChapterById(_settings.value.lastReadChapterId)
    )
    val currentChapter: StateFlow<BookChapter> = _currentChapter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<SearchResultItem>> = _searchResults.asStateFlow()

    private val _featuredQuoteIndex = MutableStateFlow(0)
    val featuredQuoteIndex: StateFlow<Int> = _featuredQuoteIndex.asStateFlow()

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private val _exportedJsonText = MutableStateFlow<String?>(null)
    val exportedJsonText: StateFlow<String?> = _exportedJsonText.asStateFlow()

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highlights: StateFlow<List<HighlightEntity>> = repository.highlightsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingHistory: StateFlow<List<ReadingHistoryEntity>> = repository.readingHistoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Record initial chapter visit if none exists
        viewModelScope.launch {
            val ch = _currentChapter.value
            repository.recordChapterVisit(
                chapterId = ch.id,
                badgeLabel = ch.badgeLabel,
                chapterTitle = ch.title,
                progressPercent = 20,
                isCompleted = false
            )
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun selectSavedSubTab(subTab: SavedSubTab) {
        _savedSubTab.value = subTab
    }

    fun openChapter(chapterId: String, navigateToReadTab: Boolean = true) {
        val chapter = BookCatalog.getChapterById(chapterId)
        _currentChapter.value = chapter
        _settings.update { it.copy(lastReadChapterId = chapter.id) }
        repository.saveSettings(_settings.value)

        if (navigateToReadTab) {
            _currentTab.value = AppNavTab.READ
        }

        viewModelScope.launch {
            val existing = readingHistory.value.find { it.chapterId == chapter.id }
            repository.recordChapterVisit(
                chapterId = chapter.id,
                badgeLabel = chapter.badgeLabel,
                chapterTitle = chapter.title,
                progressPercent = existing?.progressPercent?.coerceAtLeast(25) ?: 25,
                isCompleted = existing?.isCompleted ?: false
            )
        }
    }

    fun goToNextChapter() {
        val current = _currentChapter.value
        MarkChapterCompletedInternal(current)
        val next = BookCatalog.getNextChapter(current.id)
        if (next != null) {
            openChapter(next.id, navigateToReadTab = true)
        } else {
            showFeedback("तपाईं पुस्तकको अन्तिम खण्डमा हुनुहुन्छ।")
        }
    }

    fun goToPreviousChapter() {
        val current = _currentChapter.value
        val prev = BookCatalog.getPreviousChapter(current.id)
        if (prev != null) {
            openChapter(prev.id, navigateToReadTab = true)
        }
    }

    fun updateChapterScrollProgress(progressPercent: Int) {
        val ch = _currentChapter.value
        val existing = readingHistory.value.find { it.chapterId == ch.id }
        val completed = existing?.isCompleted == true || progressPercent >= 95
        val bestProgress = maxOf(progressPercent, existing?.progressPercent ?: 0)
        viewModelScope.launch {
            repository.recordChapterVisit(
                chapterId = ch.id,
                badgeLabel = ch.badgeLabel,
                chapterTitle = ch.title,
                progressPercent = if (completed) 100 else bestProgress,
                isCompleted = completed
            )
        }
    }

    fun toggleChapterCompleted(chapter: BookChapter = _currentChapter.value) {
        val existing = readingHistory.value.find { it.chapterId == chapter.id }
        val nowCompleted = !(existing?.isCompleted ?: false)
        viewModelScope.launch {
            repository.recordChapterVisit(
                chapterId = chapter.id,
                badgeLabel = chapter.badgeLabel,
                chapterTitle = chapter.title,
                progressPercent = if (nowCompleted) 100 else 50,
                isCompleted = nowCompleted
            )
            showFeedback(
                if (nowCompleted) "अध्याय '${chapter.title}' पूरा भएको चिह्न लगाइयो।"
                else "अध्यायको स्थिति अद्यावधिक गरियो।"
            )
        }
    }

    private fun MarkChapterCompletedInternal(chapter: BookChapter) {
        viewModelScope.launch {
            repository.recordChapterVisit(
                chapterId = chapter.id,
                badgeLabel = chapter.badgeLabel,
                chapterTitle = chapter.title,
                progressPercent = 100,
                isCompleted = true
            )
        }
    }

    fun toggleCurrentChapterBookmark() {
        val ch = _currentChapter.value
        val isBookmarked = bookmarks.value.any { it.chapterId == ch.id }
        viewModelScope.launch {
            repository.toggleBookmark(
                chapterId = ch.id,
                badgeLabel = ch.badgeLabel,
                chapterTitle = ch.title,
                subtitle = ch.subtitle,
                isCurrentlyBookmarked = isBookmarked
            )
            showFeedback(
                if (!isBookmarked) "अध्याय बुकमार्कमा सुरक्षित गरियो।"
                else "बुकमार्क हटाइयो।"
            )
        }
    }

    fun removeBookmark(chapterId: String) {
        viewModelScope.launch {
            repository.removeBookmark(chapterId)
            showFeedback("बुकमार्क हटाइयो।")
        }
    }

    fun highlightParagraph(paragraphIndex: Int, text: String, colorName: String = "GOLD") {
        val ch = _currentChapter.value
        viewModelScope.launch {
            repository.addOrUpdateHighlight(
                chapterId = ch.id,
                badgeLabel = ch.badgeLabel,
                chapterTitle = ch.title,
                paragraphIndex = paragraphIndex,
                text = text,
                colorName = colorName
            )
            showFeedback("अनुच्छेद हाइलाइट गरियो।")
        }
    }

    fun removeParagraphHighlight(paragraphIndex: Int) {
        val ch = _currentChapter.value
        viewModelScope.launch {
            repository.removeHighlightForParagraph(ch.id, paragraphIndex)
            showFeedback("हाइलाइट हटाइयो।")
        }
    }

    fun removeHighlightById(id: Int) {
        viewModelScope.launch {
            repository.removeHighlightById(id)
            showFeedback("हाइलाइट हटाइयो।")
        }
    }

    fun saveNoteForChapter(
        noteId: Int = 0,
        paragraphIndex: Int?,
        selectedPassage: String,
        noteText: String
    ) {
        val trimmed = noteText.trim()
        if (trimmed.isEmpty()) return
        val ch = _currentChapter.value
        viewModelScope.launch {
            repository.saveNote(
                id = noteId,
                chapterId = ch.id,
                badgeLabel = ch.badgeLabel,
                chapterTitle = ch.title,
                paragraphIndex = paragraphIndex,
                selectedPassage = selectedPassage,
                noteText = trimmed
            )
            showFeedback("तपाईंको व्यक्तिगत टिप्पणी (Note) सुरक्षित गरियो।")
        }
    }

    fun removeNoteById(id: Int) {
        viewModelScope.launch {
            repository.removeNoteById(id)
            showFeedback("टिप्पणी हटाइयो।")
        }
    }

    fun removeHistoryItem(chapterId: String) {
        viewModelScope.launch {
            repository.removeHistoryItem(chapterId)
            showFeedback("इतिहासबाट हटाइयो।")
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = BookCatalog.searchBook(query)
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun nextFeaturedQuote() {
        val total = BookCatalog.quotes.size
        if (total > 0) {
            _featuredQuoteIndex.value = (_featuredQuoteIndex.value + 1) % total
        }
    }

    fun previousFeaturedQuote() {
        val total = BookCatalog.quotes.size
        if (total > 0) {
            _featuredQuoteIndex.value = (_featuredQuoteIndex.value - 1 + total) % total
        }
    }

    // Settings updates
    fun setThemeMode(mode: ReaderThemeMode) {
        _settings.update { it.copy(themeMode = mode) }
        repository.saveSettings(_settings.value)
    }

    fun setFontSizeSp(sizeSp: Float) {
        _settings.update { it.copy(fontSizeSp = sizeSp.coerceIn(14f, 28f)) }
        repository.saveSettings(_settings.value)
    }

    fun setLineSpacing(multiplier: Float) {
        _settings.update { it.copy(lineSpacingMultiplier = multiplier.coerceIn(1.3f, 2.2f)) }
        repository.saveSettings(_settings.value)
    }

    fun setReadingWidthMode(widthMode: ReadingWidthMode) {
        _settings.update { it.copy(widthMode = widthMode) }
        repository.saveSettings(_settings.value)
    }

    fun setFontStyle(fontStyle: ReaderFontStyle) {
        _settings.update { it.copy(fontStyle = fontStyle) }
        repository.saveSettings(_settings.value)
    }

    fun prepareExportReadingData() {
        viewModelScope.launch {
            val json = repository.exportReadingDataToJson(_settings.value)
            _exportedJsonText.value = json
        }
    }

    fun prepareExportBookStructureJson() {
        _exportedJsonText.value = repository.exportCompleteBookStructureJson()
    }

    fun dismissExportDialog() {
        _exportedJsonText.value = null
    }

    fun importReadingDataJson(jsonString: String) {
        viewModelScope.launch {
            val result = repository.importReadingDataFromJson(jsonString)
            result.onSuccess { newSettings ->
                _settings.value = newSettings
                _currentChapter.value = BookCatalog.getChapterById(newSettings.lastReadChapterId)
                showFeedback("अध्ययन डाटा सफलतापूर्वक आयात (Import) गरियो!")
            }.onFailure {
                showFeedback("मान्य JSON ढाँचा भेटिएन। कृपया सही डाटा प्रविष्ट गर्नुहोस्।")
            }
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllReadingData()
            val defaultSettings = ReaderSettingsState()
            _settings.value = defaultSettings
            _currentChapter.value = BookCatalog.getChapterById("prologue")
            showFeedback("सम्पूर्ण अध्ययन प्रगति र संग्रह रिसेट गरियो।")
        }
    }

    fun showFeedback(message: String) {
        _feedbackMessage.value = message
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = ReaderDatabase.getInstance(context)
            val repo = ReaderRepository(db.readerDao(), context)
            return BookReaderViewModel(repo) as T
        }
    }
}
