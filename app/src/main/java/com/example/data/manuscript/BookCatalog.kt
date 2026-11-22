package com.example.data.manuscript

import com.example.R

object BookCatalog {

    val metadata = BookMetadata()

    val parts: List<BookPart> = listOf(
        BookPart(
            id = "part_front",
            partNumber = 0,
            romanNumeral = "प्रस्तावना",
            titleNepali = "प्राक्कथन र लेखकको भनाइ",
            titleEnglish = "Front Matter",
            themeSummary = "यो किताब लेखिनुको उद्देश्य, प्रश्न गर्ने निमन्त्रणा र लेखकको आन्तरिक संवाद।",
            epigraph = "मेरा शब्दहरूलाई पनि आँखा चिम्लेर विश्वास नगर्नुहोस्; मलाई पनि प्रश्न गर्नुहोस्।",
            bannerImageRes = R.drawable.img_part_awakening
        ),
        BookPart(
            id = "part_1",
            partNumber = 1,
            romanNumeral = "भाग १ • PART I",
            titleNepali = "मेरो परिवर्तनको सुरुवात",
            titleEnglish = "The Beginning of My Change",
            themeSummary = "बाल्यकालको विरासतमा पाएको विश्वास, मनमा जन्मिएका प्रश्नहरू, प्रमाण र बहुमतको भ्रमदेखि बौद्धिक स्वतन्त्रतासम्मको यात्रा।",
            epigraph = "भूगोलले तय गर्ने सत्य कहिल्यै सार्वभौम सत्य हुन सक्दैन।",
            bannerImageRes = R.drawable.img_part_awakening
        ),
        BookPart(
            id = "part_2",
            partNumber = 2,
            romanNumeral = "भाग २ • PART II",
            titleNepali = "धर्म र अन्धविश्वास",
            titleEnglish = "Religion and Superstition",
            themeSummary = "भगवान्को अवधारणा, आस्था र प्रमाणको भिन्नता, चमत्कार र संयोगको मनोविज्ञान, प्रार्थना र स्वर्ग-नरकको भयमाथिको विवेचना।",
            epigraph = "जहाँ तर्क र प्रमाण सकिन्छ, त्यहाँबाट अन्धविश्वासको डर सुरु हुनु हुँदैन—जिज्ञासा सुरु हुनुपर्छ।",
            bannerImageRes = R.drawable.img_part_awakening
        ),
        BookPart(
            id = "part_3",
            partNumber = 3,
            romanNumeral = "भाग ३ • PART III",
            titleNepali = "जातिवादको वास्तविकता",
            titleEnglish = "The Reality of Casteism",
            themeSummary = "जन्मजात उचनीचको भ्रम, शुद्धता र अशुद्धताको नाटक, ऐतिहासिक परम्परा र मन्दिरदेखि भान्सासम्मको जातीय विभेदको गम्भीर चिरफार।",
            epigraph = "मानिसको रगतमा कुनै जात लेखिएको हुँदैन; प्रत्येक शिशु केवल पूर्ण मानव भएर जन्मिन्छ।",
            bannerImageRes = R.drawable.img_part_humanity
        ),
        BookPart(
            id = "part_4",
            partNumber = 4,
            romanNumeral = "भाग ४ • PART IV",
            titleNepali = "आस्थावान् समाजसँग केही प्रश्न",
            titleEnglish = "Questions for a Believing Society",
            themeSummary = "सर्वशक्तिमान्को विरोधाभास, निर्दोषको दुःख र ईश्वरीय न्याय, अस्तित्वका दार्शनिक तर्कहरू, पाखण्ड र धार्मिक विविधतामाथि विनम्र संवाद।",
            epigraph = "म यससँग सहमत नहुन सक्छु, तर यो प्रश्नबारे मैले सोच्नुपर्छ।",
            bannerImageRes = R.drawable.img_part_awakening
        ),
        BookPart(
            id = "part_5",
            partNumber = 5,
            romanNumeral = "भाग ५ • PART V",
            titleNepali = "प्रेम, सम्बन्ध र स्वतन्त्रता",
            titleEnglish = "Love, Relationships, and Freedom",
            themeSummary = "जातभन्दा ठूलो प्रेम, अन्तरजातीय र अन्तरधार्मिक सम्बन्ध, परिवारको इज्जत बनाम व्यक्तिको स्वतन्त्रता र सम्बन्धमा समानता।",
            epigraph = "जात इतिहासको मृत बोझ हो र प्रेम वर्तमानको जीवित स्पन्दन।",
            bannerImageRes = R.drawable.img_part_humanity
        ),
        BookPart(
            id = "part_6",
            partNumber = 6,
            romanNumeral = "भाग ६ • PART VI",
            titleNepali = "भगवान् बिनाको नैतिकता",
            titleEnglish = "Morality Without God",
            themeSummary = "नैतिकताको जैविक र सामाजिक स्रोत, स्वर्गको लोभ र नरकको डरबिनाको सदाचार, वैज्ञानिक सोच, मानवीय जिम्मेवारी र मानवतावाद।",
            epigraph = "स्वर्गको लोभ र नरकको डरले गरिने भलाइ व्यापार हो; निस्वार्थ समानुभूति नै साँचो नैतिकता हो।",
            bannerImageRes = R.drawable.img_part_humanity
        ),
        BookPart(
            id = "part_7",
            partNumber = 7,
            romanNumeral = "भाग ७ • PART VII",
            titleNepali = "समाज, परिवार र म",
            titleEnglish = "Society, Family, and Me",
            themeSummary = "परिवारको आस्था र आफ्नो सोचबीचको सन्तुलन, फरक विचारको सम्मान, दोहोरो चरित्रको अन्त्य र 'म नास्तिक भएँ, अमानवीय होइन' को मर्म।",
            epigraph = "म नास्तिक भएँ भन्दैमा मेरो आँखाको आँसु सुकेको छैन र मेरो मुटुको धड्कन रोकिएको छैन।",
            bannerImageRes = R.drawable.img_author_study
        ),
        BookPart(
            id = "part_8",
            partNumber = 8,
            romanNumeral = "भाग ८ • PART VIII",
            titleNepali = "परिवर्तनपछि",
            titleEnglish = "After the Transformation",
            themeSummary = "वैचारिक रूपान्तरणको वासलात, अनिश्चितता स्वीकार्ने विनम्रता, असहमतिको स्वतन्त्रता, नयाँ पुस्तालाई सन्देश र निरन्तर प्रश्नको निष्कर्ष।",
            epigraph = "श्वास फेर्नु शरीरका लागि जति अनिवार्य छ, प्रश्न गर्नु चेतना र सभ्यताका लागि त्यति नै अनिवार्य छ।",
            bannerImageRes = R.drawable.img_part_awakening
        ),
        BookPart(
            id = "part_back",
            partNumber = 9,
            romanNumeral = "उपसंहार र सन्दर्भ",
            titleNepali = "खुला पत्र, कृतज्ञता र सन्दर्भ सामग्री",
            titleEnglish = "Epilogue & References",
            themeSummary = "पाठकका नाममा खुला पत्र, कृतज्ञता, विश्वसनीय सन्दर्भ सामग्री र लेखक समीर पराजुलीको परिचय।",
            epigraph = "आफ्नो सोच आफैं निर्माण गर।",
            bannerImageRes = R.drawable.img_author_study
        )
    )

    val allChapters: List<BookChapter> by lazy {
        buildList {
            add(ManuscriptFrontBackMatter.prologueChapter)
            add(ManuscriptFrontBackMatter.authorsNoteChapter)
            addAll(ManuscriptPart1.chapters)
            addAll(ManuscriptPart2.chapters)
            addAll(ManuscriptPart3.chapters)
            addAll(ManuscriptPart4.chapters)
            addAll(ManuscriptPart5.chapters)
            addAll(ManuscriptPart6.chapters)
            addAll(ManuscriptPart7.chapters)
            addAll(ManuscriptPart8.chapters)
            add(ManuscriptFrontBackMatter.epilogueChapter)
            add(ManuscriptFrontBackMatter.acknowledgementsChapter)
            add(ManuscriptFrontBackMatter.referencesChapter)
            add(ManuscriptFrontBackMatter.aboutAuthorChapter)
        }
    }

    private val chapterByIdMap: Map<String, BookChapter> by lazy {
        allChapters.associateBy { it.id }
    }

    private val partByIdMap: Map<String, BookPart> by lazy {
        parts.associateBy { it.id }
    }

    val quotes: List<BookQuote> = ManuscriptFrontBackMatter.featuredQuotes
    val references: List<BookReference> = ManuscriptFrontBackMatter.structuredReferences

    fun getChapterById(id: String): BookChapter {
        return chapterByIdMap[id] ?: allChapters.first()
    }

    fun getPartById(partId: String): BookPart {
        return partByIdMap[partId] ?: parts.first()
    }

    fun getChaptersForPart(partId: String): List<BookChapter> {
        return allChapters.filter { it.partId == partId }
    }

    fun getPreviousChapter(currentId: String): BookChapter? {
        val index = allChapters.indexOfFirst { it.id == currentId }
        return if (index > 0) allChapters[index - 1] else null
    }

    fun getNextChapter(currentId: String): BookChapter? {
        val index = allChapters.indexOfFirst { it.id == currentId }
        return if (index in 0 until allChapters.lastIndex) allChapters[index + 1] else null
    }

    fun searchBook(query: String): List<SearchResultItem> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        val results = mutableListOf<SearchResultItem>()

        for (chapter in allChapters) {
            val partTitle = getPartById(chapter.partId).titleNepali
            if (chapter.title.contains(trimmed, ignoreCase = true) ||
                chapter.subtitle.contains(trimmed, ignoreCase = true) ||
                chapter.badgeLabel.contains(trimmed, ignoreCase = true)
            ) {
                results.add(
                    SearchResultItem(
                        chapter = chapter,
                        partTitle = partTitle,
                        matchedSnippet = "${chapter.subtitle} — ${chapter.introduction.take(120)}...",
                        matchType = "शीर्षक"
                    )
                )
                continue
            }

            if (chapter.memorableQuote.contains(trimmed, ignoreCase = true)) {
                results.add(
                    SearchResultItem(
                        chapter = chapter,
                        partTitle = partTitle,
                        matchedSnippet = "“${chapter.memorableQuote}”",
                        matchType = "उद्धरण"
                    )
                )
                continue
            }

            if (chapter.introduction.contains(trimmed, ignoreCase = true)) {
                results.add(
                    SearchResultItem(
                        chapter = chapter,
                        partTitle = partTitle,
                        matchedSnippet = chapter.introduction,
                        matchType = "प्रस्तावना"
                    )
                )
                continue
            }

            val matchingParagraph = chapter.paragraphs.firstOrNull {
                it.contains(trimmed, ignoreCase = true)
            }
            if (matchingParagraph != null) {
                val idx = matchingParagraph.indexOf(trimmed, ignoreCase = true)
                val start = (idx - 45).coerceAtLeast(0)
                val end = (idx + trimmed.length + 90).coerceAtMost(matchingParagraph.length)
                val snippet = buildString {
                    if (start > 0) append("...")
                    append(matchingParagraph.substring(start, end))
                    if (end < matchingParagraph.length) append("...")
                }
                results.add(
                    SearchResultItem(
                        chapter = chapter,
                        partTitle = partTitle,
                        matchedSnippet = snippet,
                        matchType = "अध्याय अंश"
                    )
                )
            }
        }
        return results
    }
}
