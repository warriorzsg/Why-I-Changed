package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify book search finds relevant chapters`() {
    val results = com.example.data.manuscript.BookCatalog.searchBook("जात")
    assertTrue(results.isNotEmpty())
  }

  @Test
  fun `verify quotes exist and have valid chapters`() {
    val quotes = com.example.data.manuscript.BookCatalog.quotes
    assertTrue(quotes.isNotEmpty())
    quotes.forEach { quote ->
      val ch = com.example.data.manuscript.BookCatalog.getChapterById(quote.chapterId)
      assertNotNull(ch)
    }
  }
}
