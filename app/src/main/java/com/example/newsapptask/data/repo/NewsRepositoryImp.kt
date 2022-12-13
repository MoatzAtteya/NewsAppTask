package com.example.newsapptask.data.repo

import com.example.newsapptask.data.remote.NewsApi
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.repo.NewsRepository
import javax.inject.Inject

class NewsRepositoryImp @Inject constructor(private val api: NewsApi) : NewsRepository {

    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int): NewsResponse {
        return api.getBreakingNews(countryCode, pageNumber)
    }

    override suspend fun searchNews(searchQuery: String, pageNumber: Int): NewsResponse {
        return api.searchForNews(searchQuery, pageNumber)
    }

    override suspend fun getNewsByCategory(countryCode: String, category: String): NewsResponse {
        return api.getCategoryNews(countryCode, category)
    }
}