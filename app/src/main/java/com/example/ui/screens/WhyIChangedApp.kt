package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.manuscript.BookCatalog
import com.example.ui.ads.AdMobManager
import com.example.ui.ads.findActivity
import com.example.ui.theme.WhyIChangedTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.BookReaderViewModel

@Composable
fun WhyIChangedApp(
    viewModel: BookReaderViewModel
) {
    val activity = LocalContext.current.findActivity()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val savedSubTab by viewModel.savedSubTab.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val highlights by viewModel.highlights.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val readingHistory by viewModel.readingHistory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val featuredQuoteIndex by viewModel.featuredQuoteIndex.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val exportedJsonText by viewModel.exportedJsonText.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        val msg = feedbackMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    // Handle system back button on non-Home tabs
    BackHandler(enabled = currentTab != AppNavTab.HOME) {
        viewModel.selectTab(AppNavTab.HOME)
    }

    val featuredQuote = remember(featuredQuoteIndex) {
        BookCatalog.quotes.getOrElse(featuredQuoteIndex) { BookCatalog.quotes.first() }
    }

    WhyIChangedTheme(themeMode = settings.themeMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    AppNavTab.entries.forEach { tab ->
                        val selected = currentTab == tab
                        val icon = when (tab) {
                            AppNavTab.HOME -> if (selected) Icons.Default.Home else Icons.Outlined.Home
                            AppNavTab.READ -> if (selected) Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Outlined.MenuBook
                            AppNavTab.SAVED -> if (selected) Icons.Default.Bookmarks else Icons.Outlined.Bookmarks
                            AppNavTab.SETTINGS -> if (selected) Icons.Default.Settings else Icons.Outlined.Settings
                        }

                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "${tab.labelEnglish} (${tab.labelNepali})"
                                )
                            },
                            label = {
                                Text(
                                    text = tab.labelEnglish,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.routeId}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            when (currentTab) {
                AppNavTab.HOME -> {
                    HomeScreen(
                        currentChapter = currentChapter,
                        readingHistory = readingHistory,
                        featuredQuote = featuredQuote,
                        onContinueReading = { viewModel.selectTab(AppNavTab.READ) },
                        onSelectChapter = { chapterId ->
                            viewModel.openChapter(chapterId, navigateToReadTab = true)
                        },
                        onNextQuote = { viewModel.nextFeaturedQuote() },
                        onPrevQuote = { viewModel.previousFeaturedQuote() },
                        onShowFeedback = { viewModel.showFeedback(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppNavTab.READ -> {
                    ReadScreen(
                        chapter = currentChapter,
                        settings = settings,
                        bookmarks = bookmarks,
                        highlights = highlights,
                        notes = notes,
                        readingHistory = readingHistory,
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        onSelectChapter = { chapterId ->
                            viewModel.openChapter(chapterId, navigateToReadTab = true)
                        },
                        onPreviousChapter = { viewModel.goToPreviousChapter() },
                        onNextChapter = {
                            val hasNext = BookCatalog.getNextChapter(currentChapter.id) != null
                            viewModel.goToNextChapter()
                            if (hasNext) {
                                activity?.let { AdMobManager.showInterstitialIfReady(it) }
                            }
                        },
                        onToggleBookmark = { viewModel.toggleCurrentChapterBookmark() },
                        onToggleChapterCompleted = { viewModel.toggleChapterCompleted() },
                        onUpdateScrollProgress = { viewModel.updateChapterScrollProgress(it) },
                        onHighlightParagraph = { idx, text, color ->
                            viewModel.highlightParagraph(idx, text, color)
                        },
                        onRemoveParagraphHighlight = { idx ->
                            viewModel.removeParagraphHighlight(idx)
                        },
                        onSaveNote = { id, pIdx, passage, noteText ->
                            viewModel.saveNoteForChapter(id, pIdx, passage, noteText)
                        },
                        onDeleteNote = { id -> viewModel.removeNoteById(id) },
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onClearSearch = { viewModel.clearSearch() },
                        onThemeChange = { viewModel.setThemeMode(it) },
                        onFontSizeChange = { viewModel.setFontSizeSp(it) },
                        onLineSpacingChange = { viewModel.setLineSpacing(it) },
                        onWidthModeChange = { viewModel.setReadingWidthMode(it) },
                        onFontStyleChange = { viewModel.setFontStyle(it) },
                        onShowFeedback = { viewModel.showFeedback(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppNavTab.SAVED -> {
                    SavedScreen(
                        selectedSubTab = savedSubTab,
                        bookmarks = bookmarks,
                        highlights = highlights,
                        notes = notes,
                        readingHistory = readingHistory,
                        onSelectSubTab = { viewModel.selectSavedSubTab(it) },
                        onOpenChapter = { chapterId ->
                            viewModel.openChapter(chapterId, navigateToReadTab = true)
                        },
                        onRemoveBookmark = { viewModel.removeBookmark(it) },
                        onRemoveHighlight = { viewModel.removeHighlightById(it) },
                        onRemoveNote = { viewModel.removeNoteById(it) },
                        onRemoveHistory = { viewModel.removeHistoryItem(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                AppNavTab.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        exportedJsonText = exportedJsonText,
                        onThemeChange = { viewModel.setThemeMode(it) },
                        onFontSizeChange = { viewModel.setFontSizeSp(it) },
                        onLineSpacingChange = { viewModel.setLineSpacing(it) },
                        onWidthModeChange = { viewModel.setReadingWidthMode(it) },
                        onFontStyleChange = { viewModel.setFontStyle(it) },
                        onExportReadingData = { viewModel.prepareExportReadingData() },
                        onExportBookJson = { viewModel.prepareExportBookStructureJson() },
                        onDismissExportDialog = { viewModel.dismissExportDialog() },
                        onImportReadingData = { viewModel.importReadingDataJson(it) },
                        onResetProgress = { viewModel.resetAllProgress() },
                        onOpenChapter = { chapterId ->
                            viewModel.openChapter(chapterId, navigateToReadTab = true)
                        },
                        onShowFeedback = { viewModel.showFeedback(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
