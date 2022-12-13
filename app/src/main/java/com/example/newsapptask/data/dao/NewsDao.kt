package com.example.newsapptask.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapptask.domain.model.Article

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSavedArticles(article: Article)

    @Query("SELECT * FROM ARTICLES ORDER BY publishedAt")
    fun getSavedArticles() : List<Article>
}