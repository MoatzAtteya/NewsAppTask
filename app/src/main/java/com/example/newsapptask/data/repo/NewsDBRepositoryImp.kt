package com.example.newsapptask.data.repo

import com.example.newsapptask.data.dao.NewsDao
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.repo.NewsDBRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsDBRepositoryImp @Inject constructor(val dao : NewsDao) : NewsDBRepository {
    override suspend fun saveArticleDB(article: Article)  : Long {
       return dao.insertArticle(article)
    }

    override suspend fun deleteArticleDB(article: Article) {
        dao.deleteArticle(article)
    }

    override suspend fun deleteArticleByIdDB(id: Long) {
        dao.deleteArticleById(id)
    }

    override suspend fun getSavedArticlesByURL(url: String): List<Article> {
        return dao.getSavedArticlesByURL(url)
    }

    override suspend fun getSavedArticles(): Flow<List<Article>> {
        return dao.getSavedArticles()
    }


}