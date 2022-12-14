package com.example.newsapptask.data.dao

import androidx.room.*
import com.example.newsapptask.domain.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedArticle(articles: List<Article>)

    @Query("SELECT * FROM ARTICLES ORDER BY publishedAt")
    fun getCachedArticles() : Flow<List<Article>>

    @Query("SELECT * FROM ARTICLES WHERE isSaved=:isSaved ORDER BY publishedAt")
    fun getSavedArticles(isSaved : Boolean = true) : Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM ARTICLES WHERE id=:id")
    suspend fun deleteArticleById(id : Long)

    @Query("DELETE FROM ARTICLES WHERE isSaved=:isSaved")
    suspend fun deleteAllArticles(isSaved: Boolean = false)

    @Query("SELECT * FROM ARTICLES WHERE url=:url AND isSaved=:isSaved")
    fun getSavedArticlesByURL(url : String , isSaved: Boolean = true) : List<Article>



}