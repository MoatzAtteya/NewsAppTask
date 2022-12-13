package com.example.newsapptask.domain.use_case

import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.repo.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCategoryNewsUseCase @Inject constructor(private val repository: NewsRepository) {

    fun invoke(countryCode: String,category : String): Flow<Resource<List<Article>>> = flow {
        try {
            val newsResponse = repository.getNewsByCategory(countryCode, category)
            if (newsResponse.status == "ok")
                emit(Resource.Success(newsResponse.articles))
            else
                emit(Resource.Error("An unexpected error occurred."))

        } catch (ex: HttpException) {
            emit(Resource.Error(ex.localizedMessage ?: "An unexpected error occurred."))
        } catch (ex: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
        }
    }
}