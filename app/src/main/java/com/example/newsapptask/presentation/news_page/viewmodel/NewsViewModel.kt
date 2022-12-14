package com.example.newsapptask.presentation.news_page.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapptask.common.Resource
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.model.NewsResponse
import com.example.newsapptask.domain.use_case.DeleteArticleUseCase
import com.example.newsapptask.domain.use_case.GetBreakingNewsUseCase
import com.example.newsapptask.domain.use_case.GetCategoryNewsUseCase
import com.example.newsapptask.domain.use_case.SaveArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetBreakingNewsUseCase,
    private val getCategoryNewsUseCase: GetCategoryNewsUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
    private val deleteArticleUseCase: DeleteArticleUseCase
) :
    ViewModel() {

    private val _getNewsResponse = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val getNewsResponse get() = _getNewsResponse

    private val _getCategoryNewsResponse =
        MutableStateFlow<Resource<List<Article>>>(Resource.Loading())
    val getCategoryNewsResponse get() = _getCategoryNewsResponse

    private val _saveArticleResponse = MutableStateFlow<Resource<Long>>(Resource.Loading())
    val saveArticleResponse get() = _saveArticleResponse



    var breakingNewsPage = 1
    private var newsResponse: NewsResponse? = null
    private var country = "us"

    private var count = 3
    private var favouriteCategories = arrayListOf("sport", "health", "science")
    private var categoryArticles: MutableList<Article> = mutableListOf()

    init {
        getBreakingNews()
        getAndReturnCategoryNews()
    }

    private fun getBreakingNews() = viewModelScope.launch {
        getNewsUseCase.invoke(country, breakingNewsPage).collect { response ->
            when (response) {
                is Resource.Error -> _getNewsResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> {
                    _getNewsResponse.value = Resource.Success(response.data!!)
                }
            }

        }
    }

    private fun getAndReturnCategoryNews() = viewModelScope.launch {
        for (category in favouriteCategories) {
            getCategoryNews(category)
        }
    }

    private suspend fun getCategoryNews(category: String) {

        getCategoryNewsUseCase.invoke(country, category).collect { response ->
            when (response) {
                is Resource.Error -> _getCategoryNewsResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> {
                    _getCategoryNewsResponse.value = Resource.Loading()
                }
                is Resource.Success -> {
                   /* breakingNewsPage++
                    if (newsResponse == null)
                        newsResponse = response.data
                    else {
                        val oldArticles = newsResponse?.articles
                        val newArticles = response.data!!.articles
                        oldArticles?.addAll(newArticles)
                    }*/
                    categoryArticles.addAll(response.data!!.articles)
                    count--
                    if (count == 0)
                        _getCategoryNewsResponse.value = Resource.Success(categoryArticles)
                }
            }
        }
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        saveArticleUseCase.invoke(article).collect{response->
            when(response){
                is Resource.Error -> _saveArticleResponse.value = Resource.Error(response.message!!)
                is Resource.Loading -> {}
                is Resource.Success -> _saveArticleResponse.value = Resource.Success(response.data!!)
            }
        }
    }

    fun deleteArticle(id : Long) = viewModelScope.launch {
        deleteArticleUseCase.deleteArticleById(id).collect{response->
            when(response){
                is Resource.Error -> {
                    Log.e(TAG,response.message!!)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Log.d(TAG,response.data!!)
                }
            }
        }
    }
companion object{
    private const val TAG = "NewsViewModel"
}
}