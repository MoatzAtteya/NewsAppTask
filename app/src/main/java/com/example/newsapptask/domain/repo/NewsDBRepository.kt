package com.example.newsapptask.domain.repo

import com.example.newsapptask.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsDBRepository {

    suspend fun saveArticleDB(article: Article): Long

    suspend fun deleteArticleDB(article: Article)

    suspend fun getSavedArticles(): Flow<List<Article>>

    suspend fun deleteArticleByIdDB(id: Long)

    suspend fun getSavedArticlesByURL(url : String) : List<Article>
}