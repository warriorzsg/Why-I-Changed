package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        HighlightEntity::class,
        NoteEntity::class,
        ReadingHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ReaderDatabase : RoomDatabase() {

    abstract fun readerDao(): ReaderDao

    companion object {
        @Volatile
        private var INSTANCE: ReaderDatabase? = null

        fun getInstance(context: Context): ReaderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReaderDatabase::class.java,
                    "why_i_changed_reader.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
