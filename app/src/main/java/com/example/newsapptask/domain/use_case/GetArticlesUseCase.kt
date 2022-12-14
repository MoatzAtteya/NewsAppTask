package com.example.newsapptask.domain.use_case

import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.repo.NewsDBRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(private val repository : NewsDBRepository ) {

    fun invoke() : Flow<Resource<List<Article>>> = flow {
        try {
            repository.getSavedArticles().collect{
                emit(Resource.Success(it))
            }
        }catch (ex : Exception){
            ex.printStackTrace()
            emit(Resource.Error(ex.message.toString()))
        }
    }
}