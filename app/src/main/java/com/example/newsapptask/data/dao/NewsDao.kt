package com.example.newsapptask.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapptask.domain.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedArticles(article: Article) : Long

    @Query("SELECT * FROM ARTICLES ORDER BY publishedAt")
    fun getSavedArticles() : Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM ARTICLES WHERE id=:id")
    suspend fun deleteArticleById(id : Long)

    @Query("DELETE FROM ARTICLES")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM ARTICLES WHERE url=:url")
    fun getSavedArticlesByURL(url : String) : List<Article>
}