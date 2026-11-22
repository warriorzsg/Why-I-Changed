package com.example.data.manuscript

import com.example.R

data class BookMetadata(
    val title: String = "WHY I CHANGED",
    val subtitle: String = "मैले आफ्नो सोच किन परिवर्तन गरेँ?",
    val author: String = "Samir Parajuli",
    val authorNepali: String = "समीर पराजुली",
    val language: String = "Nepali",
    val genre: String = "दर्शन, सामाजिक समालोचना, आत्मचिन्तन र मानवता (Philosophy, Social Commentary, Personal Reflection, Humanity)",
    val edition: String = "प्रथम डिजिटल संस्करण (First Digital Edition)",
    val shortDescription: String = "विश्वास, परम्परा, जातपात, आस्था, प्रेम र मानवताबारे गरिएको एक इमानदार दार्शनिक आत्मसमीक्षा। यो किताब कसैलाई के सोच्ने भनेर सिकाउन होइन, बरु हामीले के सोचिरहेका छौँ र किन सोचिरहेका छौँ भनी आफैंलाई प्रश्न गर्न प्रेरित गर्न लेखिएको हो।"
)

data class BookPart(
    val id: String,
    val partNumber: Int, // 0 for Front Matter, 1..8 for Parts I..VIII, 9 for Back Matter
    val romanNumeral: String,
    val titleNepali: String,
    val titleEnglish: String,
    val themeSummary: String,
    val epigraph: String,
    val bannerImageRes: Int
)

data class BookChapter(
    val id: String,
    val chapterNumber: Int?, // null for Prologue, Author's Note, Epilogue, etc.; 1..40 for main chapters
    val displayOrder: Int,
    val partId: String,
    val badgeLabel: String, // e.g., "अध्याय १" or "प्राक्कथन (Prologue)"
    val title: String,
    val subtitle: String,
    val introduction: String,
    val paragraphs: List<String>,
    val reflectionQuestion: String,
    val memorableQuote: String,
    val references: List<String>,
    val readingTimeMinutes: Int,
    val bannerImageRes: Int? = null
)

data class BookQuote(
    val id: String,
    val text: String,
    val chapterId: String,
    val chapterTitle: String,
    val theme: String
)

data class BookReference(
    val id: String,
    val category: String,
    val title: String,
    val authorOrSource: String,
    val year: String,
    val relevanceNote: String
)

enum class ReaderThemeMode(val labelNepali: String, val labelEnglish: String) {
    DARK("अँध्यारो (Dark)", "Charcoal & Gold"),
    LIGHT("उज्यालो (Light)", "Warm Ivory"),
    SEPIA("सेपिया (Sepia)", "Classic Parchment")
}

enum class ReadingWidthMode(val labelNepali: String, val horizontalPaddingDp: Int) {
    COMPACT("संकुचित (Focused)", 32),
    BALANCED("सन्तुलित (Balanced)", 20),
    WIDE("फराकिलो (Wide)", 12)
}

enum class ReaderFontStyle(val labelNepali: String) {
    SERIF("साहित्यिक सेरिफ (Literary)"),
    SANS("आधुनिक सफा (Modern Mukta)")
}

data class ReaderSettingsState(
    val themeMode: ReaderThemeMode = ReaderThemeMode.DARK,
    val fontSizeSp: Float = 18f,
    val lineSpacingMultiplier: Float = 1.75f,
    val widthMode: ReadingWidthMode = ReadingWidthMode.BALANCED,
    val fontStyle: ReaderFontStyle = ReaderFontStyle.SANS,
    val lastReadChapterId: String = "prologue"
)

data class SearchResultItem(
    val chapter: BookChapter,
    val partTitle: String,
    val matchedSnippet: String,
    val matchType: String // "शीर्षक", "उद्धरण", "अंश"
)
