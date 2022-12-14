package com.example.newsapptask.presentation.saved_news_page.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.use_case.DeleteArticleUseCase
import com.example.newsapptask.domain.use_case.GetArticlesUseCase
import com.example.newsapptask.domain.use_case.SaveArticleUseCase
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedNewsViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase
) :
    ViewModel() {

    private val _getSavedNewsResponse =
        MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val getSavedNewsResponse get() = _getSavedNewsResponse

    private val _deleteArticleResponse = MutableStateFlow<Resource<String>>(Resource.Loading())
    val deleteArticleResponse get() = _deleteArticleResponse

    fun getSavedArticles() = viewModelScope.launch {
        getArticlesUseCase.invoke().collect { response ->
            when (response) {
                is Resource.Error -> _getSavedNewsResponse.value =
                    Resource.Error(response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> _getSavedNewsResponse.value =
                    Resource.Success(response.data!!)
            }
        }
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        deleteArticleUseCase.invoke(article).collect { response ->
            when (response) {
                is Resource.Error -> _deleteArticleResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> _deleteArticleResponse.value = Resource.Success(response.data!!)

            }
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        saveArticleUseCase.invoke(article).collect{response->
            when(response){
                is Resource.Error -> Log.e(TAG, response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> Log.d(TAG, response.data.toString())
            }
        }
    }

    companion object {
        private const val TAG = "SavedNewsViewModel"
    }

}