package com.example.newsapptask.presentation.search_page.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.use_case.DeleteArticleUseCase
import com.example.newsapptask.domain.use_case.SaveArticleUseCase
import com.example.newsapptask.domain.use_case.SearchArticleUseCase
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArticleUseCase: SearchArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase
) :
    ViewModel() {

    private val _getNewsResponse = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val getNewsResponse get() = _getNewsResponse

    private val _saveArticleResponse = MutableStateFlow<Resource<Long>>(Resource.Loading())
    val saveArticleResponse get() = _saveArticleResponse


    private var count = 0
    private var searchArticles: MutableList<Article> = mutableListOf()

    fun getAndReturnNews(categories: ArrayList<String>, searchText: String) =
        viewModelScope.launch {
            count = categories.size
            searchArticles.clear()
            for (category in categories) {
                searchArticles(category, searchText)
            }
        }

    private fun searchArticles(category: String, searchText: String) = viewModelScope.launch {
        searchArticleUseCase.invoke(searchText, 1, category).collect { response ->
            when (response) {
                is Resource.Error -> _getNewsResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> _getNewsResponse.value = Resource.Loading()
                is Resource.Success -> {
                    count--
                    searchArticles.addAll(response.data!!.articles)
                    if (count == 0){
                        _getNewsResponse.value = Resource.Success(searchArticles)
                    }
                }
            }
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        saveArticleUseCase.invoke(article).collect { response ->
            when (response) {
                is Resource.Error -> _saveArticleResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> _saveArticleResponse.value =
                    Resource.Success(response.data!!)
            }
        }
    }

    fun deleteArticle(id: Long) = viewModelScope.launch {
        deleteArticleUseCase.deleteArticleById(id).collect { response ->
            when (response) {
                is Resource.Error -> {
                    Log.e(TAG, response.message!!)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d(TAG, response.data!!)
                }
            }
        }
    }
    companion object{
        private const val TAG = "SearchViewModel"
    }
}