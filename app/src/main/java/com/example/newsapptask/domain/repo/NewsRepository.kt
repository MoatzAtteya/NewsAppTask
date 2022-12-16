package com.example.newsapptask.domain.repo

import com.example.newsapptask.domain.model.NewsResponse

interface NewsRepository {

    // This interface has all functions make api calls.

    suspend fun getBreakingNews(countryCode : String , pageNumber : Int) : NewsResponse

    suspend fun searchNews(searchQuery : String , pageNumber: Int , category: String) : NewsResponse

    suspend fun getNewsByCategory(countryCode: String,category : String) : NewsResponse


}