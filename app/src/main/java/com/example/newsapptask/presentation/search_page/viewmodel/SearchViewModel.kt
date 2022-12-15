package com.example.newsapptask.presentation.search_page.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapptask.common.Constants
import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.use_case.DeleteArticleUseCase
import com.example.newsapptask.domain.use_case.SaveArticleUseCase
import com.example.newsapptask.domain.use_case.SearchArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArticleUseCase: SearchArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    application: Application
) :
    ViewModel() {

    private val _getNewsResponse = MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val getNewsResponse get() = _getNewsResponse

    private val _saveArticleResponse = MutableStateFlow<Resource<Long>>(Resource.Loading())
    val saveArticleResponse get() = _saveArticleResponse


    private var count = 0
    private var searchArticles: MutableList<Article> = mutableListOf()

    private val sharedPreferences = application.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )


    fun getAndReturnNews(categories: MutableSet<String>, searchText: String) =
        viewModelScope.launch {
            count = categories.size
            searchArticles.clear()
            for (category in categories) {
                searchArticles(category, searchText)
            }
        }

    fun searchArticles(category: String, searchText: String) = viewModelScope.launch {
        searchArticleUseCase.invoke(searchText, 1, category).collect { response ->
            when (response) {
                is Resource.Error -> _getNewsResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> _getNewsResponse.value = Resource.Loading()
                is Resource.Success -> {
                    count--
                    searchArticles.addAll(response.data!!.articles)
                    if (count == 0) {
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

    fun deleteArticle(article: Article) = viewModelScope.launch {
        deleteArticleUseCase.deleteArticleById(article).collect { response ->
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

    fun getPreferencesCategories(): MutableSet<String>? {
        return sharedPreferences.getStringSet(Constants.CATEGORIES, mutableSetOf())
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }
}