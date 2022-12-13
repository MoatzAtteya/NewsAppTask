package com.example.newsapptask.domain.use_case

import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.repo.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetBreakingNewsUseCase @Inject constructor(private val repository: NewsRepository) {

    fun invoke(countryCode: String, pageNumber: Int): Flow<Resource<NewsResponse>> = flow {
        try {
            val newsResponse = repository.getBreakingNews(countryCode, pageNumber)
            if (newsResponse.status == "ok")
                emit(Resource.Success(newsResponse))
            else
                emit(Resource.Error("An unexpected error occurred."))

        } catch (ex: HttpException) {
            emit(Resource.Error(ex.localizedMessage ?: "An unexpected error occurred."))
        } catch (ex: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
        }
    }

}