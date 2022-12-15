package com.example.newsapptask.presentation.first_time_page.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.newsapptask.common.Constants
import com.example.newsapptask.presentation.first_time_page.ui.OnBoardActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardViewModel @Inject constructor(val application: Application) : ViewModel() {

    fun savePreferences(countryName : String , countryCode:String , categories :  MutableSet<String>) {
        val sharedPreferences =
            application.getSharedPreferences(
                Constants.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences.edit()
        editor.putString(Constants.COUNTRY_NAME,countryName)
        editor.putString(Constants.COUNTRY_CODE, countryCode)
        editor.putStringSet(Constants.CATEGORIES, categories)
        editor.putBoolean(Constants.FIRST_TIME_OPEN , false)
        editor.apply()
    }
}