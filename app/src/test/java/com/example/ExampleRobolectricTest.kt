package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Why I Changed", appName)
  }

  @Test
  fun `verify book manuscript completeness`() {
    val chapters = com.example.data.manuscript.BookCatalog.allChapters
    // 40 main chapters + Prologue + Author's Note + Epilogue + Acknowledgements + References + About the Author = 46 sections
    assertEquals(46, chapters.size)
    val part1Chapters = com.example.data.manuscript.BookCatalog.getChaptersForPart("part_1")
    assertEquals(5, part1Chapters.size)
  }
}
