package com.example.newsapptask.presentation.setting_page.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.newsapptask.common.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(application: Application) : ViewModel() {

    private val sharedPreferences = application.getSharedPreferences(
        Constants.SHARED_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun getPreferencesCountryName(): String? {
        return sharedPreferences.getString(Constants.COUNTRY_NAME , "")
    }


    fun getPreferencesCountryCode(): String? {
        return sharedPreferences.getString(Constants.COUNTRY_CODE , "")
    }

    fun getPreferencesCategories(): MutableSet<String>? {
        return sharedPreferences.getStringSet(Constants.CATEGORIES , mutableSetOf())
    }
}