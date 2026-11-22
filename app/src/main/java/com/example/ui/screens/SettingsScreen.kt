package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.manuscript.BookCatalog
import com.example.data.manuscript.ReaderFontStyle
import com.example.data.manuscript.ReaderSettingsState
import com.example.data.manuscript.ReaderThemeMode
import com.example.data.manuscript.ReadingWidthMode
import com.example.ui.theme.LoraFontFamily
import com.example.ui.theme.MuktaFontFamily

@Composable
fun SettingsScreen(
    settings: ReaderSettingsState,
    exportedJsonText: String?,
    onThemeChange: (ReaderThemeMode) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onWidthModeChange: (ReadingWidthMode) -> Unit,
    onFontStyleChange: (ReaderFontStyle) -> Unit,
    onExportReadingData: () -> Unit,
    onExportBookJson: () -> Unit,
    onDismissExportDialog: () -> Unit,
    onImportReadingData: (String) -> Unit,
    onResetProgress: () -> Unit,
    onOpenChapter: (String) -> Unit,
    onShowFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showReferencesDialog by remember { mutableStateOf(false) }

    val previewFontFamily = when (settings.fontStyle) {
        ReaderFontStyle.SERIF -> LoraFontFamily
        ReaderFontStyle.SANS -> MuktaFontFamily
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .testTag("settings_list"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "सेटिङ्स र पठन प्राथमिकता (Settings)",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "आफ्नो आँखा र सहजताअनुसार अक्षर, थिम र डाटा व्यवस्थापन गर्नुहोस्",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 1. THEMES SECTION (Dark, Light, Sepia)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "रङ र थिम (Themes: Dark, Light, Sepia)",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReaderThemeMode.entries.forEach { mode ->
                                val isSelected = settings.themeMode == mode
                                Surface(
                                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onThemeChange(mode) }
                                        .testTag("theme_option_${mode.name}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = mode.labelNepali,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = mode.labelEnglish,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. READING TYPOGRAPHY CONTROLS
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "अध्ययन र अक्षर सेटिङ्स (Reading Controls)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Live Preview Box
                        Surface(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "“आफ्नो सोच आफैं निर्माण गर—किनभने पिँजडा सुनकै भए पनि पखेटाको धर्म आकाश नाप्नु नै हो।”",
                                fontFamily = previewFontFamily,
                                fontSize = settings.fontSizeSp.sp,
                                lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(
                                    horizontal = settings.widthMode.horizontalPaddingDp.dp,
                                    vertical = 14.dp
                                )
                            )
                        }

                        // Font Size Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("अक्षरको आकार (Font Size)", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${settings.fontSizeSp.toInt()} sp",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.fontSizeSp,
                                onValueChange = onFontSizeChange,
                                valueRange = 14f..28f,
                                modifier = Modifier.testTag("settings_font_size_slider")
                            )
                        }

                        // Line Spacing Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("हरफबीचको दूरी (Line Spacing)", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    String.format("%.2fx", settings.lineSpacingMultiplier),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.lineSpacingMultiplier,
                                onValueChange = onLineSpacingChange,
                                valueRange = 1.3f..2.2f,
                                modifier = Modifier.testTag("settings_line_spacing_slider")
                            )
                        }

                        // Reading Width
                        Text("पृष्ठको चौडाइ (Reading Width)", style = MaterialTheme.typography.bodyMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReadingWidthMode.entries.forEach { widthMode ->
                                FilterChip(
                                    selected = settings.widthMode == widthMode,
                                    onClick = { onWidthModeChange(widthMode) },
                                    label = { Text(widthMode.labelNepali, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Font Style
                        Text("लिपि शैली (Typography Style)", style = MaterialTheme.typography.bodyMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ReaderFontStyle.entries.forEach { fontStyle ->
                                FilterChip(
                                    selected = settings.fontStyle == fontStyle,
                                    onClick = { onFontStyleChange(fontStyle) },
                                    label = { Text(fontStyle.labelNepali, fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 3. DATA MANAGEMENT (Export, Import, Reset)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "डाटा व्यवस्थापन (Data & Backup)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "तपाईंका बुकमार्क, हाइलाइट, टिप्पणी र अध्ययन प्रगति पूर्णतः अफलाइन (Local Storage / Room) मा सुरक्षित छन्।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onExportReadingData,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_data_button")
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("डाटा निर्यात (Export)")
                            }

                            OutlinedButton(
                                onClick = { showImportDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("import_data_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("डाटा आयात (Import)")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onExportBookJson,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("पुस्तक JSON संरचना")
                            }

                            OutlinedButton(
                                onClick = { showResetConfirmDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reset_progress_button")
                            ) {
                                Icon(
                                    Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("प्रगति रिसेट गर्नुहोस्", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // 4. ABOUT THE BOOK, AUTHOR, REFERENCES & COPYRIGHT
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "पुस्तक र लेखकको बारेमा (About)",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "शीर्षक: ${BookCatalog.metadata.title} (${BookCatalog.metadata.subtitle})\nलेखक: ${BookCatalog.metadata.authorNepali} (${BookCatalog.metadata.author})\nविधा: ${BookCatalog.metadata.genre}\nसंस्करण: ${BookCatalog.metadata.edition}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                        Text(
                            text = "लेखकको परिचय: समीर पराजुली विचार, दर्शन, सामाजिक समालोचना र स्वतन्त्र चिन्तनमा रुचि राख्ने लेखक हुन्। प्रस्तुत कृतिमा उनले अन्धविश्वास, जातीय विभेद र डरमा आधारित परम्पराको समालोचना गर्दै प्रश्न, तर्क र मानवीय करुणामा आधारित स्वतन्त्र सोचको पक्षपोषण गरेका छन्।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onOpenChapter("about_author") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("लेखकको पूर्ण अध्याय")
                            }

                            OutlinedButton(
                                onClick = { showReferencesDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("सन्दर्भ सामग्री सूची")
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                        Text(
                            text = "प्रतिलिपि अधिकार (Copyright) © समीर पराजुली (Samir Parajuli)। सर्वाधिकार सुरक्षित।\nApp Version: 1.0.0 (Offline Digital Book Edition)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Export JSON Dialog
    if (exportedJsonText != null) {
        AlertDialog(
            onDismissRequest = onDismissExportDialog,
            title = { Text("निर्यात गरिएको JSON डाटा (Exported Data)") },
            text = {
                OutlinedTextField(
                    value = exportedJsonText,
                    onValueChange = {},
                    readOnly = true,
                    maxLines = 12,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, exportedJsonText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Reading Data JSON"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("सेयर")
                    }
                    Button(
                        onClick = {
                            copyTextToClipboard(context, exportedJsonText)
                            onShowFeedback("JSON डाटा क्लिपबोर्डमा कपी गरियो।")
                            onDismissExportDialog()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("कपी गर्नुहोस्")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissExportDialog) {
                    Text("बन्द गर्नुहोस्")
                }
            }
        )
    }

    // Import JSON Dialog
    if (showImportDialog) {
        var jsonInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("अध्ययन डाटा आयात गर्नुहोस् (Import JSON)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "पहिले निर्यात (Export) गरिएको JSON डाटा तल पेस्ट गर्नुहोस्:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        placeholder = { Text("{ \"bookTitle\": \"WHY I CHANGED\", ... }") },
                        minLines = 5,
                        maxLines = 10,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onImportReadingData(jsonInput)
                        showImportDialog = false
                    }
                ) {
                    Text("आयात गर्नुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("रद्द गर्नुहोस्")
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("अध्ययन प्रगति रिसेट गर्ने हो?") },
            text = {
                Text("यसले तपाईंका सबै बुकमार्क, हाइलाइट, व्यक्तिगत टिप्पणी र अध्ययन इतिहास मेटाउनेछ। के तपाईं निश्चित हुनुहुन्छ?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("हो, रिसेट गर्नुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("रद्द गर्नुहोस्")
                }
            }
        )
    }

    // Structured References Dialog
    if (showReferencesDialog) {
        AlertDialog(
            onDismissRequest = { showReferencesDialog = false },
            title = { Text("सन्दर्भ सामग्रीहरू (References)") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(BookCatalog.references.size) { idx ->
                        val ref = BookCatalog.references[idx]
                        Column {
                            Text(
                                text = "${idx + 1}. ${ref.title} (${ref.year})",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "स्रोत/लेखक: ${ref.authorOrSource} • ${ref.category}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = ref.relevanceNote,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReferencesDialog = false
                        onOpenChapter("references_chapter")
                    }
                ) {
                    Text("पूर्ण सन्दर्भ अध्याय पढ्नुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReferencesDialog = false }) {
                    Text("बन्द गर्नुहोस्")
                }
            }
        )
    }
}
