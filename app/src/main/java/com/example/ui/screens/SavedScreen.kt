package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.local.HighlightEntity
import com.example.data.local.NoteEntity
import com.example.data.local.ReadingHistoryEntity
import com.example.ui.viewmodel.SavedSubTab

@Composable
fun SavedScreen(
    selectedSubTab: SavedSubTab,
    bookmarks: List<BookmarkEntity>,
    highlights: List<HighlightEntity>,
    notes: List<NoteEntity>,
    readingHistory: List<ReadingHistoryEntity>,
    onSelectSubTab: (SavedSubTab) -> Unit,
    onOpenChapter: (String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onRemoveHighlight: (Int) -> Unit,
    onRemoveNote: (Int) -> Unit,
    onRemoveHistory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "तपाईंको संग्रह (Saved & History)",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "बुकमार्क गरिएका अध्यायहरू, हाइलाइटहरू, व्यक्तिगत टिप्पणी र अध्ययन इतिहास",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-tab filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SavedSubTab.entries.forEach { tab ->
                    val count = when (tab) {
                        SavedSubTab.BOOKMARKS -> bookmarks.size
                        SavedSubTab.HIGHLIGHTS -> highlights.size
                        SavedSubTab.NOTES -> notes.size
                        SavedSubTab.HISTORY -> readingHistory.size
                    }
                    FilterChip(
                        selected = selectedSubTab == tab,
                        onClick = { onSelectSubTab(tab) },
                        label = {
                            Text(
                                text = "${tab.labelNepali} ($count)",
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("saved_subtab_${tab.name}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedSubTab) {
                SavedSubTab.BOOKMARKS -> {
                    if (bookmarks.isEmpty()) {
                        EmptySavedState(
                            icon = Icons.Default.Bookmark,
                            title = "कुनै बुकमार्क राखिएको छैन",
                            subtitle = "अध्ययन गर्दा मन परेका अध्यायहरू जोगाउन माथिको बुकमार्क आइकन थिच्नुहोस्।"
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            modifier = Modifier.testTag("bookmarks_list")
                        ) {
                            items(bookmarks, key = { it.chapterId }) { item ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenChapter(item.chapterId) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.badgeLabel,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = item.chapterTitle,
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = item.subtitle,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        IconButton(
                                            onClick = { onRemoveBookmark(item.chapterId) },
                                            modifier = Modifier.testTag("delete_bookmark_${item.chapterId}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Remove bookmark",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SavedSubTab.HIGHLIGHTS -> {
                    if (highlights.isEmpty()) {
                        EmptySavedState(
                            icon = Icons.Default.Highlight,
                            title = "कुनै हाइलाइट सुरक्षित छैन",
                            subtitle = "पढ्दै गर्दा कुनै अनुच्छेदमा छोएर (Tap गरेर) रङ छानी हाइलाइट गर्नुहोस्।"
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            modifier = Modifier.testTag("highlights_list")
                        ) {
                            items(highlights, key = { it.id }) { item ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenChapter(item.chapterId) }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.badgeLabel} • ${item.chapterTitle}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { onRemoveHighlight(item.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete highlight",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "“${item.highlightedText}”",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                SavedSubTab.NOTES -> {
                    if (notes.isEmpty()) {
                        EmptySavedState(
                            icon = Icons.Default.EditNote,
                            title = "कुनै व्यक्तिगत टिप्पणी छैन",
                            subtitle = "पढ्दा मनमा उठेका विचार वा प्रश्नहरू अध्यायभित्र नोट आइकन थिचेर टिप्नुहोस्।"
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            modifier = Modifier.testTag("notes_list")
                        ) {
                            items(notes, key = { it.id }) { item ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenChapter(item.chapterId) }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.badgeLabel} • ${item.chapterTitle}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { onRemoveNote(item.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete note",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (item.selectedPassage.isNotBlank()) {
                                            Text(
                                                text = "सन्दर्भ: “${item.selectedPassage}”",
                                                fontSize = 12.sp,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }

                                        Text(
                                            text = item.noteText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                SavedSubTab.HISTORY -> {
                    if (readingHistory.isEmpty()) {
                        EmptySavedState(
                            icon = Icons.Default.History,
                            title = "अध्ययन इतिहास खाली छ",
                            subtitle = "तपाईंले पढ्नुभएका अध्यायहरूको सूची र प्रगति यहाँ देखिनेछ।"
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            modifier = Modifier.testTag("history_list")
                        ) {
                            items(readingHistory, key = { it.chapterId }) { item ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onOpenChapter(item.chapterId) }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = item.badgeLabel,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    if (item.isCompleted) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Completed",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = item.chapterTitle,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            IconButton(onClick = { onRemoveHistory(item.chapterId) }) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Remove from history",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            LinearProgressIndicator(
                                                progress = { (item.progressPercent / 100f).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(6.dp)
                                                    .clip(CircleShape),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            Text(
                                                text = "${item.progressPercent}%",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
