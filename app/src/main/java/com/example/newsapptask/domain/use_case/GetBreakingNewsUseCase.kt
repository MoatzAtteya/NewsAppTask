package com.example.newsapptask.domain.use_case

import androidx.room.withTransaction
import com.example.newsapptask.data.remote.NewsDatabase
import com.example.newsapptask.data.remote.networkBoundResource
import com.example.newsapptask.data.repo.NewsDBRepositoryImp
import com.example.newsapptask.data.repo.NewsRepositoryImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBreakingNewsUseCase @Inject constructor(
    private val newsDBRepositoryImp: NewsDBRepositoryImp,
    private val newsRepositoryImp: NewsRepositoryImp,
    private val appDatabase: NewsDatabase
) {

    fun invoke(countryCode: String, pageNumber: Int) = networkBoundResource(
        query = {
            newsDBRepositoryImp.getCachedArticles()
        },
        fetch = {
            newsRepositoryImp.getBreakingNews(countryCode, pageNumber)
        },
        saveFetchResult = { newsResponse ->
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase.withTransaction {
                    newsDBRepositoryImp.deleteAllArticles()
                    newsDBRepositoryImp.saveCachedArticles(newsResponse.articles)
                }
            }
        }
    )

}