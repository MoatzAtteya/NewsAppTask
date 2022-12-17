package com.example.newsapptask.presentation.news_page.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapptask.common.Constants
import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.use_case.DeleteArticleUseCase
import com.example.newsapptask.domain.use_case.GetBreakingNewsUseCase
import com.example.newsapptask.domain.use_case.GetCategoryNewsUseCase
import com.example.newsapptask.domain.use_case.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getCategoryNewsUseCase: GetCategoryNewsUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase,
    getBreakingNewsUseCase: GetBreakingNewsUseCase,
    application: Application
) :
    ViewModel() {

    private val _getCategoryNewsResponse =
        MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val getCategoryNewsResponse get() = _getCategoryNewsResponse

    private val _saveArticleResponse = MutableStateFlow<Resource<Long>>(Resource.Loading())
    val saveArticleResponse get() = _saveArticleResponse

    private val sharedPreferences = application.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    var breakingNewsPage = 1
    private var country = sharedPreferences.getString(Constants.COUNTRY_CODE, "")

    /*
     This variable will be the cached data if the no internet or the incoming data is the same.
     or it will be the incoming api data.
     */

    val articles = getBreakingNewsUseCase.invoke(country!!, breakingNewsPage).asLiveData()


    //getting stored category value.
    private var favouriteCategories =
        sharedPreferences.getStringSet(Constants.CATEGORIES, mutableSetOf())!!.toList()

    //Add each category articles to this list, in the end emit the whole data.
    private var categoryArticles: MutableList<Article> = mutableListOf()

    init {
        getAndReturnCategoryNews()
    }


    /*
        Instead of looping on the category list 3 times to call getCategoryNews(),
        and then emit the total articles.
        we call getCategoryNews() 3 time in parallel to reduce the time, then emit the total articles.
     */
    private fun getAndReturnCategoryNews() = viewModelScope.launch {
        val job1 = async { getCategoryNews(favouriteCategories[0]) }
        val job2 = async { getCategoryNews(favouriteCategories[1]) }
        val job3 = async { getCategoryNews(favouriteCategories[2]) }
        job1.await()
        job2.await()
        job3.await()
        _getCategoryNewsResponse.value =
            Resource.Success(categoryArticles.sortedByDescending { it.publishedAt })

    }

    private suspend fun getCategoryNews(category: String) {

        getCategoryNewsUseCase.invoke(country!!, category).collect { response ->
            when (response) {
                is Resource.Error -> _getCategoryNewsResponse.value =
                    Resource.Error(response.message!!)
                is Resource.Loading -> {
                    _getCategoryNewsResponse.value = Resource.Loading()
                }
                is Resource.Success -> {
                    categoryArticles.addAll(response.data!!.articles)
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

    companion object {
        private const val TAG = "NewsViewModel"
    }
}