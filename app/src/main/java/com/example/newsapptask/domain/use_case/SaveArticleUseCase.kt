package com.example.newsapptask.domain.use_case

import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.repo.NewsDBRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveArticleUseCase @Inject constructor(private val repository: NewsDBRepository) {

    fun invoke(article: Article): Flow<Resource<Long>> = flow {
        try {
            article.isSaved = true
            val articles = repository.getSavedArticlesByURL(article.url!!)
            if (articles.isNotEmpty()){
                emit(Resource.Success(-1))
            }else{
                val id = repository.saveArticleDB(article)
                emit(Resource.Success(id))
            }
        } catch (ex: Exception) {
            emit(Resource.Error(ex.message.toString()))
        }
    }

}