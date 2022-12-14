package com.example.newsapptask.domain.use_case

import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.repo.NewsDBRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteArticleUseCase @Inject constructor(private val repository: NewsDBRepository) {
    fun invoke(article: Article): Flow<Resource<String>> = flow {
        try {
            repository.deleteArticleDB(article)
            emit(Resource.Success("Article deleted successfully."))
        } catch (ex: Exception) {
            emit(Resource.Error(ex.message.toString()))
        }
    }

    fun deleteArticleById(id: Long): Flow<Resource<String>> = flow {
        try {
            repository.deleteArticleByIdDB(id)
            emit(Resource.Success("Article deleted successfully."))
        } catch (ex: Exception) {
            emit(Resource.Error(ex.message.toString()))
        }
    }
}