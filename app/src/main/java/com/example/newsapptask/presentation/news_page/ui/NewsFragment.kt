package com.example.newsapptask.presentation.news_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsapptask.common.Constants.QUERY_PAGE_SIZE
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentNewsBinding
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newsViewModel =
            ViewModelProvider(this).get(NewsViewModel::class.java)
        _binding = FragmentNewsBinding.inflate(inflater, container, false)


        var isLoading = false
        var isLastPage = false
        var isScrolling = true

        GlobalScope.launch(Dispatchers.IO) {
            newsViewModel.getNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main){
                            Toast.makeText(requireContext(), response.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val totalPages = (response.data!!.totalResults / QUERY_PAGE_SIZE + 2).toFloat().roundToInt()
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        Log.d(TAG,"news are: ${response.data}")
                        // TODO: attach the articles to the recyclerView in Dispatcher.main *to access the ui*.
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO){
            newsViewModel.getCategoryNewsResponse.collect{response->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main){
                            Toast.makeText(requireContext(), response.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Log.d(TAG,"category news are: ${response.data!!.size}")
                        // TODO: attach the articles to the recyclerView in Dispatcher.main *to access the ui*.
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "NewsFragment"
    }
}