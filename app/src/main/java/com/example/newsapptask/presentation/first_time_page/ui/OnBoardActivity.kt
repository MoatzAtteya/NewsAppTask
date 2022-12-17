package com.example.newsapptask.presentation.first_time_page.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapptask.MainActivity
import com.example.newsapptask.R
import com.example.newsapptask.common.Constants
import com.example.newsapptask.databinding.ActivityOnBoardBinding
import com.example.newsapptask.domain.model.Country
import com.example.newsapptask.presentation.first_time_page.adapter.CountryAdapter
import com.example.newsapptask.presentation.first_time_page.viewmodel.OnBoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var countryAdapter: CountryAdapter
    lateinit var binding: ActivityOnBoardBinding
    lateinit var onBoardViewModel: OnBoardViewModel
    private var countryName = ""
    private var countryCode = ""
    private var categories = mutableSetOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        onBoardViewModel = ViewModelProvider(this)[OnBoardViewModel::class.java]

        setContentView(binding.root)

        setUpCountryRv()
        setUpCategoriesSpinner()
        supportActionBar?.hide()

        binding.tvTitleClearSelectedCategory.setOnClickListener(this)
        binding.btnSaveCategories.setOnClickListener(this)
    }


    private fun setUpCategoriesSpinner() {
        binding.spinner.apply {
            setItems(Constants.categories)
            setOnItemSelectedListener { view, position, id, item ->
                if (categories.size == 3) {
                    Toast.makeText(context, getString(R.string.max_category_error_msg), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    categories.add(item.toString())
                    println(categories)
                }
                binding.tvSelectedCategory.text = categories.toString()
            }
        }
    }

    private fun setUpCountryRv() {
        countryAdapter = CountryAdapter(this, Constants.countries)
        binding.rvSelectCountry.apply {
            adapter = countryAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        countryAdapter.setOnItemClickListener(object : CountryAdapter.OnItemClickListener {
            override fun onCountryClicked(position: Int, country: Country) {
                countryName = country.name
                countryCode = country.code
                binding.tvSelectCountry.text = country.name
                Toast.makeText(
                    this@OnBoardActivity,
                    getString(R.string.selected_country_info_msg,countryName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tvTitleClearSelectedCategory->{
                categories.clear()
                countryName = ""
                countryCode = ""
                binding.tvSelectedCategory.text = ""
                binding.tvSelectCountry.text = ""
            }
            R.id.btnSaveCategories->{
                if (categories.size != 3) {
                    Toast.makeText(this, getString(R.string.select_category_error_msg), Toast.LENGTH_SHORT).show()
                    return
                }
                if (countryName.isEmpty()) {
                    Toast.makeText(this, getString(R.string.select_country_error_msg), Toast.LENGTH_SHORT).show()
                    return
                }
                onBoardViewModel.savePreferences(countryName, countryCode, categories)
                Toast.makeText(this, getString(R.string.preferences_saved_msg), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

}