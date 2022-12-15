package com.example.newsapptask.presentation.setting_page.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.newsapptask.R
import com.example.newsapptask.databinding.FragmentSettingBinding
import com.example.newsapptask.presentation.setting_page.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

         updateUI()

        binding.tvChangeFavCategory.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_setting_to_onBoardActivity)
        }

        return binding.root
    }

    private fun updateUI() {
        binding.tvSelectedCountry.text = viewModel.getPreferencesCountryName()
        binding.tvFavCategory.text = viewModel.getPreferencesCategories().toString()
    }


}