package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.HighlightEntity
import com.example.data.local.NoteEntity
import com.example.data.local.ReadingHistoryEntity
import com.example.data.manuscript.BookCatalog
import com.example.data.manuscript.BookChapter
import com.example.data.manuscript.ReaderFontStyle
import com.example.data.manuscript.ReaderSettingsState
import com.example.data.manuscript.ReaderThemeMode
import com.example.data.manuscript.ReadingWidthMode
import com.example.data.manuscript.SearchResultItem
import com.example.ui.theme.HighlightAmberBgDark
import com.example.ui.theme.HighlightAmberBgLight
import com.example.ui.theme.HighlightGoldBgDark
import com.example.ui.theme.HighlightGoldBgLight
import com.example.ui.theme.HighlightRoseBgDark
import com.example.ui.theme.HighlightRoseBgLight
import com.example.ui.theme.HighlightSageBgDark
import com.example.ui.theme.HighlightSageBgLight
import com.example.ui.theme.LoraFontFamily
import com.example.ui.theme.MuktaFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadScreen(
    chapter: BookChapter,
    settings: ReaderSettingsState,
    bookmarks: List<BookmarkEntity>,
    highlights: List<HighlightEntity>,
    notes: List<NoteEntity>,
    readingHistory: List<ReadingHistoryEntity>,
    searchQuery: String,
    searchResults: List<SearchResultItem>,
    onSelectChapter: (String) -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleChapterCompleted: () -> Unit,
    onUpdateScrollProgress: (Int) -> Unit,
    onHighlightParagraph: (Int, String, String) -> Unit,
    onRemoveParagraphHighlight: (Int) -> Unit,
    onSaveNote: (Int, Int?, String, String) -> Unit,
    onDeleteNote: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onThemeChange: (ReaderThemeMode) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onWidthModeChange: (ReadingWidthMode) -> Unit,
    onFontStyleChange: (ReaderFontStyle) -> Unit,
    onShowFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val isBookmarked = remember(bookmarks, chapter.id) {
        bookmarks.any { it.chapterId == chapter.id }
    }
    val isChapterCompleted = remember(readingHistory, chapter.id) {
        readingHistory.any { it.chapterId == chapter.id && it.isCompleted }
    }
    val chapterHighlights = remember(highlights, chapter.id) {
        highlights.filter { it.chapterId == chapter.id }
    }
    val chapterNotes = remember(notes, chapter.id) {
        notes.filter { it.chapterId == chapter.id }
    }

    val part = remember(chapter.partId) {
        BookCatalog.getPartById(chapter.partId)
    }
    val prevChapter = remember(chapter.id) {
        BookCatalog.getPreviousChapter(chapter.id)
    }
    val nextChapter = remember(chapter.id) {
        BookCatalog.getNextChapter(chapter.id)
    }

    var showTocSheet by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showQuickTypographySheet by remember { mutableStateOf(false) }
    var activeParagraphIndex by remember { mutableStateOf<Int?>(null) }

    // Note dialog state
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteDialogParagraphIndex by remember { mutableStateOf<Int?>(null) }
    var noteDialogPassage by remember { mutableStateOf("") }
    var noteDialogExistingText by remember { mutableStateOf("") }
    var noteDialogExistingId by remember { mutableStateOf(0) }

    // Scroll to top when chapter changes
    LaunchedEffect(chapter.id) {
        activeParagraphIndex = null
        listState.scrollToItem(0)
    }

    // Calculate live scroll progress
    val scrollProgressFraction by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems <= 1) 0.1f
            else {
                val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                ((lastVisible + 1).toFloat() / totalItems.toFloat()).coerceIn(0.05f, 1f)
            }
        }
    }

    LaunchedEffect(scrollProgressFraction, chapter.id) {
        val pct = (scrollProgressFraction * 100).toInt()
        if (pct >= 85) {
            onUpdateScrollProgress(pct)
        }
    }

    val readerFontFamily = when (settings.fontStyle) {
        ReaderFontStyle.SERIF -> LoraFontFamily
        ReaderFontStyle.SANS -> MuktaFontFamily
    }
    val bodyFontSize = settings.fontSizeSp.sp
    val bodyLineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp
    val horizontalPad = settings.widthMode.horizontalPaddingDp.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. TOP READER BAR
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // TOC Button
                    TextButton(
                        onClick = { showTocSheet = true },
                        modifier = Modifier.testTag("reader_toc_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                            contentDescription = "Table of Contents",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "विषयसूची",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Chapter Progress indicator text
                    Text(
                        text = "${chapter.displayOrder} / ${BookCatalog.allChapters.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Action Icons: Search, Typography, Add Note, Bookmark
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showSearchSheet = true },
                            modifier = Modifier.testTag("reader_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search in book",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = { showQuickTypographySheet = true },
                            modifier = Modifier.testTag("reader_typography_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Typography and theme controls",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = {
                                noteDialogExistingId = 0
                                noteDialogParagraphIndex = null
                                noteDialogPassage = chapter.title
                                noteDialogExistingText = ""
                                showNoteDialog = true
                            },
                            modifier = Modifier.testTag("reader_add_note_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Add chapter note",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark chapter",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Live chapter reading progress bar
                LinearProgressIndicator(
                    progress = { scrollProgressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // 2. DISTRACTION-FREE BOOK READER CANVAS
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 740.dp)
                    .testTag("reader_content_list"),
                contentPadding = PaddingValues(
                    start = horizontalPad,
                    end = horizontalPad,
                    top = 24.dp,
                    bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Chapter Part & Title Header
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${part.romanNumeral} • ${part.titleNepali}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = chapter.badgeLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "अध्ययन समय: ~${chapter.readingTimeMinutes} मिनेट",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = readerFontFamily,
                                fontSize = (settings.fontSizeSp + 8f).sp,
                                lineHeight = ((settings.fontSizeSp + 8f) * 1.35f).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.testTag("reader_chapter_title")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = chapter.subtitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = readerFontFamily
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Optional thematic illustration on key chapters
                        if (chapter.bannerImageRes != null) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Image(
                                painter = painterResource(id = chapter.bannerImageRes),
                                contentDescription = chapter.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Chapter Introduction Callout with Gold vertical rule
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .heightIn(min = 68.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = chapter.introduction,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = readerFontFamily,
                                    fontSize = bodyFontSize,
                                    lineHeight = bodyLineHeight,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "सुझाव: कुनै पनि अनुच्छेदमा छोएर (Tap गरेर) हाइलाइट वा व्यक्तिगत टिप्पणी (Note) राख्न सक्नुहुन्छ।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                }

                // Chapter Paragraphs
                itemsIndexed(chapter.paragraphs) { index, paragraphText ->
                    val existingHighlight = chapterHighlights.find { it.paragraphIndex == index }
                    val paragraphNotes = chapterNotes.filter { it.paragraphIndex == index }
                    val isSelected = activeParagraphIndex == index

                    val highlightBgColor = if (existingHighlight != null) {
                        resolveHighlightColor(existingHighlight.colorName, settings.themeMode)
                    } else {
                        Color.Transparent
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(highlightBgColor)
                            .clickable {
                                activeParagraphIndex = if (isSelected) null else index
                            }
                            .padding(
                                horizontal = if (existingHighlight != null || isSelected) 10.dp else 0.dp,
                                vertical = if (existingHighlight != null || isSelected) 8.dp else 2.dp
                            )
                            .testTag("reader_paragraph_$index")
                    ) {
                        Text(
                            text = paragraphText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = readerFontFamily,
                                fontSize = bodyFontSize,
                                lineHeight = bodyLineHeight
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Show attached notes for this paragraph if any
                        paragraphNotes.forEach { note ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "तपाईंको टिप्पणी (Personal Note):",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = note.noteText,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteNote(note.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete note",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Inline Paragraph Action Bar (Highlight, Note, Copy)
                        AnimatedVisibility(visible = isSelected) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "अनुच्छेद विकल्प (Highlight / Note):",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        // Color dots for highlighting
                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            listOf(
                                                "GOLD" to Color(0xFFD4AF37),
                                                "AMBER" to Color(0xFFE07A5F),
                                                "SAGE" to Color(0xFF81B29A),
                                                "ROSE" to Color(0xFFC97A8E)
                                            ).forEach { (colorName, dotColor) ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(26.dp)
                                                        .clip(CircleShape)
                                                        .background(dotColor)
                                                        .border(
                                                            width = if (existingHighlight?.colorName == colorName) 2.dp else 1.dp,
                                                            color = if (existingHighlight?.colorName == colorName)
                                                                MaterialTheme.colorScheme.onSurface
                                                            else Color.Transparent,
                                                            shape = CircleShape
                                                        )
                                                        .clickable {
                                                            onHighlightParagraph(index, paragraphText, colorName)
                                                            activeParagraphIndex = null
                                                        }
                                                        .testTag("highlight_color_$colorName")
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                noteDialogExistingId = 0
                                                noteDialogParagraphIndex = index
                                                noteDialogPassage = paragraphText.take(140)
                                                noteDialogExistingText = ""
                                                showNoteDialog = true
                                                activeParagraphIndex = null
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("टिप्पणी लेख्नुहोस्", fontSize = 12.sp)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                copyTextToClipboard(context, paragraphText)
                                                onShowFeedback("अनुच्छेद कपी गरियो।")
                                                activeParagraphIndex = null
                                            },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy paragraph",
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("कपी", fontSize = 12.sp)
                                        }

                                        if (existingHighlight != null) {
                                            TextButton(
                                                onClick = {
                                                    onRemoveParagraphHighlight(index)
                                                    activeParagraphIndex = null
                                                }
                                            ) {
                                                Text("हटाउनुहोस्", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Memorable Quote Box
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "यस अध्यायको मूल मर्म (KEY QUOTE)",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        copyTextToClipboard(
                                            context,
                                            "“${chapter.memorableQuote}” — समीर पराजुली (${chapter.title})"
                                        )
                                        onShowFeedback("उद्धरण कपी गरियो।")
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy chapter quote",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = "“${chapter.memorableQuote}”",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = readerFontFamily,
                                    fontStyle = FontStyle.Italic
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Philosophical Reflection Question Box
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "आत्मसमीक्षा र मननका लागि प्रश्न",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = chapter.reflectionQuestion,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = readerFontFamily,
                                    fontSize = (settings.fontSizeSp - 1f).sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Chapter-level Notes if any
                val generalChapterNotes = chapterNotes.filter { it.paragraphIndex == null }
                if (generalChapterNotes.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "यस अध्यायमा तपाईंका टिप्पणीहरू:",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            generalChapterNotes.forEach { note ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.noteText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { onDeleteNote(note.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete note",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Chapter References if present
                if (chapter.references.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Text(
                                text = "सन्दर्भ स्रोतहरू (References):",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            chapter.references.forEach { ref ->
                                Text(
                                    text = "• $ref",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Chapter Completion & Previous/Next Navigation Footer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onToggleChapterCompleted,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("mark_completed_button")
                            ) {
                                Icon(
                                    imageVector = if (isChapterCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isChapterCompleted) "अध्ययन पूरा भयो" else "पूरा भएको चिह्न लगाउनुहोस्",
                                    fontSize = 13.sp
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    noteDialogExistingId = 0
                                    noteDialogParagraphIndex = null
                                    noteDialogPassage = chapter.title
                                    noteDialogExistingText = ""
                                    showNoteDialog = true
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("टिप्पणी थप्नुहोस्", fontSize = 13.sp)
                            }
                        }

                        // Previous & Next Chapter Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onPreviousChapter,
                                enabled = prevChapter != null,
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("prev_chapter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous chapter",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text("अघिल्लो अध्याय", fontSize = 11.sp)
                                    Text(
                                        text = prevChapter?.title ?: "सुरुवात",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Button(
                                onClick = onNextChapter,
                                enabled = nextChapter != null,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("next_chapter_button")
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("अर्को अध्याय", fontSize = 11.sp)
                                    Text(
                                        text = nextChapter?.title ?: "अन्त्य",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next chapter",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. TABLE OF CONTENTS MODAL SHEET
    if (showTocSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTocSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            TableOfContentsSheetContent(
                currentChapterId = chapter.id,
                readingHistory = readingHistory,
                onSelectChapter = { selectedId ->
                    showTocSheet = false
                    onSelectChapter(selectedId)
                },
                onClose = { showTocSheet = false }
            )
        }
    }

    // 4. IN-BOOK SEARCH MODAL SHEET
    if (showSearchSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSearchSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            BookSearchSheetContent(
                query = searchQuery,
                results = searchResults,
                onQueryChange = onSearchQueryChange,
                onClearQuery = onClearSearch,
                onSelectResult = { selectedChapterId ->
                    showSearchSheet = false
                    onSelectChapter(selectedChapterId)
                },
                onClose = { showSearchSheet = false }
            )
        }
    }

    // 5. QUICK TYPOGRAPHY & THEME SHEET
    if (showQuickTypographySheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickTypographySheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            QuickTypographySheetContent(
                settings = settings,
                onThemeChange = onThemeChange,
                onFontSizeChange = onFontSizeChange,
                onLineSpacingChange = onLineSpacingChange,
                onWidthModeChange = onWidthModeChange,
                onFontStyleChange = onFontStyleChange
            )
        }
    }

    // 6. PERSONAL NOTE DIALOG
    if (showNoteDialog) {
        var noteTextState by remember(showNoteDialog) { mutableStateOf(noteDialogExistingText) }
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = {
                Text(
                    text = "व्यक्तिगत टिप्पणी (Personal Note)",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "सन्दर्भ: “${noteDialogPassage.take(100)}”",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = noteTextState,
                        onValueChange = { noteTextState = it },
                        label = { Text("आफ्नो विचार वा प्रश्न यहाँ लेख्नुहोस्...") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_dialog_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveNote(
                            noteDialogExistingId,
                            noteDialogParagraphIndex,
                            noteDialogPassage,
                            noteTextState
                        )
                        showNoteDialog = false
                    },
                    modifier = Modifier.testTag("note_dialog_save_button")
                ) {
                    Text("सुरक्षित गर्नुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) {
                    Text("रद्द गर्नुहोस्")
                }
            }
        )
    }
}

@Composable
private fun TableOfContentsSheetContent(
    currentChapterId: String,
    readingHistory: List<ReadingHistoryEntity>,
    onSelectChapter: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 640.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "विषयसूची (Table of Contents)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "WHY I CHANGED — ८ भाग, ४० अध्याय र अतिरिक्त खण्डहरू",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close TOC")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toc_list"),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BookCatalog.parts.forEach { part ->
                item(key = "header_${part.id}") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${part.romanNumeral} — ${part.titleNepali}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = part.titleEnglish,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val partChapters = BookCatalog.getChaptersForPart(part.id)
                items(partChapters, key = { it.id }) { ch ->
                    val isCurrent = ch.id == currentChapterId
                    val isDone = readingHistory.any { it.chapterId == ch.id && it.isCompleted }

                    Surface(
                        color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectChapter(ch.id) }
                            .testTag("toc_item_${ch.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${ch.badgeLabel} • ${ch.readingTimeMinutes} मिनेट",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = ch.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = ch.subtitle,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookSearchSheetContent(
    query: String,
    results: List<SearchResultItem>,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSelectResult: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 420.dp, max = 640.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "पुस्तकभित्र खोज्नुहोस् (Search Book)",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close search")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("जस्तै: जात, स्वतन्त्रता, प्रार्थना, नैतिकता, प्रेम...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("book_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick suggestion chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("प्रश्न", "जात", "प्रेम", "नैतिकता", "मानवता").forEach { suggestion ->
                FilterChip(
                    selected = query == suggestion,
                    onClick = { onQueryChange(suggestion) },
                    label = { Text(suggestion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (query.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "४० वटै अध्याय र उद्धरणहरूमा नेपाली वा अङ्ग्रेजी शब्द खोज्नुहोस्।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "“$query” सँग मिल्ने कुनै अंश भेटिएन।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "${results.size} परिणामहरू भेटिए:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(results, key = { "${it.chapter.id}_${it.matchType}" }) { item ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectResult(item.chapter.id) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.chapter.badgeLabel} • ${item.partTitle}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = item.matchType,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = item.chapter.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.matchedSnippet,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTypographySheetContent(
    settings: ReaderSettingsState,
    onThemeChange: (ReaderThemeMode) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onWidthModeChange: (ReadingWidthMode) -> Unit,
    onFontStyleChange: (ReaderFontStyle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "पठन शैली र थिम (Reading Preferences)",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Theme selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReaderThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = settings.themeMode == mode,
                    onClick = { onThemeChange(mode) },
                    label = { Text(mode.labelNepali) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Font Size
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("अक्षरको आकार (Font Size)", style = MaterialTheme.typography.bodyMedium)
                Text("${settings.fontSizeSp.toInt()} sp", color = MaterialTheme.colorScheme.primary)
            }
            Slider(
                value = settings.fontSizeSp,
                onValueChange = onFontSizeChange,
                valueRange = 14f..28f
            )
        }

        // Line Spacing
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("हरफबीचको दूरी (Line Spacing)", style = MaterialTheme.typography.bodyMedium)
                Text(
                    String.format("%.2fx", settings.lineSpacingMultiplier),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = settings.lineSpacingMultiplier,
                onValueChange = onLineSpacingChange,
                valueRange = 1.3f..2.2f
            )
        }

        // Font Style & Width
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReaderFontStyle.entries.forEach { style ->
                FilterChip(
                    selected = settings.fontStyle == style,
                    onClick = { onFontStyleChange(style) },
                    label = { Text(style.labelNepali) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReadingWidthMode.entries.forEach { width ->
                FilterChip(
                    selected = settings.widthMode == width,
                    onClick = { onWidthModeChange(width) },
                    label = { Text(width.labelNepali, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

private fun resolveHighlightColor(colorName: String, themeMode: ReaderThemeMode): Color {
    val isDark = themeMode == ReaderThemeMode.DARK
    return when (colorName) {
        "AMBER" -> if (isDark) HighlightAmberBgDark else HighlightAmberBgLight
        "SAGE" -> if (isDark) HighlightSageBgDark else HighlightSageBgLight
        "ROSE" -> if (isDark) HighlightRoseBgDark else HighlightRoseBgLight
        else -> if (isDark) HighlightGoldBgDark else HighlightGoldBgLight
    }
}
