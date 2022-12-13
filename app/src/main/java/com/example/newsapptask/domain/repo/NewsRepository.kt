package com.example.newsapptask.domain.repo

import com.example.newsapptask.domain.model.NewsResponse

interface NewsRepository {

    suspend fun getBreakingNews(countryCode : String , pageNumber : Int) : NewsResponse

    suspend fun searchNews(searchQuery : String , pageNumber: Int) : NewsResponse

    suspend fun getNewsByCategory(countryCode: String,category : String) : NewsResponse


}