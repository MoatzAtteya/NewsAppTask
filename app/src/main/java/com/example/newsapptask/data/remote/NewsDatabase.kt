package com.example.newsapptask.data.remote

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapptask.common.Converters
import com.example.newsapptask.data.dao.NewsDao
import com.example.newsapptask.domain.model.Article

@Database(entities = [Article::class], version = 1, exportSchema = false)

@TypeConverters(Converters::class)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

}