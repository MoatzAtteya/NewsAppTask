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
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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

    /*
        The default search category are 3 categories.
        Add each category articles to this list, in the end emit the whole data.
     */
    private var searchArticles: MutableList<Article> = mutableListOf()

    private val sharedPreferences = application.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )


    fun getAndReturnNews(categories: MutableSet<String>, searchText: String) =
        viewModelScope.launch {
            count = categories.size
            searchArticles.clear()
            /*
                - if the count is 3 which means that the user is searching
                  with his favourite categories -> call searchArticles() 3 times with different
                  category in parallel.
                - if the count is 1 this means that the user select 1 category from the spinner.
             */
            if (count > 1){
                val job1 = async { searchArticles(categories.toList()[0],searchText) }
                val job2 = async { searchArticles(categories.toList()[1],searchText) }
                val job3 = async { searchArticles(categories.toList()[2],searchText) }
                job1.await()
                job2.await()
                job3.await()
                delay(1000)
                _getNewsResponse.value = Resource.Success(searchArticles)
            }else{
                searchArticles(categories.toList()[0],searchText)
                delay(1000)
                _getNewsResponse.value = Resource.Success(searchArticles)
            }

        }

    private fun searchArticles(category: String, searchText: String) = viewModelScope.launch {
        searchArticleUseCase.invoke(searchText, 1, category).collect { response ->
            when (response) {
                is Resource.Error -> _getNewsResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> _getNewsResponse.value = Resource.Loading()
                is Resource.Success -> {
                    searchArticles.addAll(response.data!!.articles)
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