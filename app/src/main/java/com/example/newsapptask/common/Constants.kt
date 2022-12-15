package com.example.newsapptask.common

import com.example.newsapptask.R
import com.example.newsapptask.domain.model.Country

object Constants {
    const val API_KEY = "efd09619da20400ebca69a294caeaad9"
    const val BASE_URL = "https://newsapi.org"
    const val SEARCH_NEWS_TIME_DELAY = 500L
    const val QUERY_PAGE_SIZE = 20
    const val FIRST_TIME_OPEN = "first_time_open"
    const val COUNTRY_NAME = "country_name"
    const val COUNTRY_CODE = "country_code"
    const val CATEGORIES = "categories"
    const val SHARED_PREFERENCES_NAME = "Preferences"

    val countries = arrayListOf<Country>(
        Country(R.drawable.icons8_egypt_96, "EGY", "eg"),
        Country(R.drawable.icons8_united_arab_emirates_96, "UAE", "ae"),
        Country(R.drawable.icons8_saudi_arabia, "Saudi Arabia", "sa"),
        Country(R.drawable.icons8_great_britain_96, "United Kingdom", "gb"),
        Country(R.drawable.icons8_morocco_96, "Morocco", "ma"),
        Country(R.drawable.icons8_ukraine_96, "Ukraine", "ua"),
        Country(R.drawable.icons8_russian_federation_96, "Russia", "ru"),
        Country(R.drawable.american_flag, "United States", "us"),
    )

    val categories = arrayListOf(
        "general", "business", "entertainment", "health", "science", "sports", "technology"
    )
}