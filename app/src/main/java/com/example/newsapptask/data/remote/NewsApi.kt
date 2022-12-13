package com.example.newsapptask.data.remote

import com.example.newsapptask.common.Constants.API_KEY
import com.example.newsapptask.domain.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode : String,
        @Query("Page") pageNumber : Int = 1,
        @Query("apiKey") apiKey : String = API_KEY
    ) : NewsResponse

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery : String,
        @Query("Page") pageNumber : Int = 1,
        @Query("apiKey") apiKey : String = API_KEY
    ) :NewsResponse

    @GET("v2/top-headlines")
    suspend fun getCategoryNews(
        @Query("country") countryCode: String,
        @Query("category") category : String,
        @Query("apiKey") apiKey: String = API_KEY
    ) : NewsResponse



}