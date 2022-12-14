package com.example.newsapptask.di

import android.content.Context
import androidx.room.Room
import com.example.newsapptask.common.Constants
import com.example.newsapptask.data.dao.NewsDao
import com.example.newsapptask.data.remote.NewsApi
import com.example.newsapptask.data.remote.NewsDatabase
import com.example.newsapptask.data.repo.NewsDBRepositoryImp
import com.example.newsapptask.data.repo.NewsRepositoryImp
import com.example.newsapptask.domain.repo.NewsDBRepository
import com.example.newsapptask.domain.repo.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(logger)

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(api: NewsApi): NewsRepository {
        return NewsRepositoryImp(api)
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext appContext: Context): NewsDatabase {
        return Room.databaseBuilder(
            appContext,
            NewsDatabase::class.java,
            "asd"
        ).allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun providesNewsDao(appDatabase: NewsDatabase): NewsDao {
        return appDatabase.newsDao()
    }

    @Provides
    @Singleton
    fun provideNewsDBRepository(newsDao: NewsDao): NewsDBRepository {
        return NewsDBRepositoryImp(newsDao)

    }
}