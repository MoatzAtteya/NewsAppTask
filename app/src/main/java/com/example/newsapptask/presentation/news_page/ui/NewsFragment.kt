package com.example.newsapptask.presentation.news_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.newsapptask.common.Constants.QUERY_PAGE_SIZE
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentNewsBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.news_page.adapter.BreakingNewsAdapter
import com.example.newsapptask.presentation.news_page.adapter.CategoryNewsAdapter
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var breakingNewsAdapter: BreakingNewsAdapter
    private lateinit var categoryNewsAdapter: CategoryNewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var snackbar: Snackbar
    private var articleID: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        newsViewModel =
            ViewModelProvider(this)[NewsViewModel::class.java]
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        setUpBreakingNewsRv()
        setUpCategoryNewsRv()

        snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Article Saved Successfully.",
            Snackbar.LENGTH_SHORT
        )
        //var isLoading = false
        //var isLastPage = false
        //var isScrolling = true

        GlobalScope.launch(Dispatchers.IO) {
            newsViewModel.getNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        //  val totalPages = (response.data!!.totalResults / QUERY_PAGE_SIZE + 2).toFloat().roundToInt()
                        //  isLastPage = newsViewModel.breakingNewsPage == totalPages
                        //  Log.d(TAG,"news are: ${response.data}")
                        withContext(Dispatchers.Main) {
                            breakingNewsAdapter.differ.submitList(response.data!!.articles.toList())
                        }
                    }
                }
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            newsViewModel.getCategoryNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "category news are: ${response.data!!.size}")
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            categoryNewsAdapter.differ.submitList(response.data.toList())
                        }
                    }
                }
            }
        }

        binding.ivSortBy.setOnClickListener {
            Toast.makeText(requireContext(), "Sorted by published dated.", Toast.LENGTH_SHORT)
                .show()
        }

        return binding.root
    }

    private fun setUpBreakingNewsRv() {
        breakingNewsAdapter = BreakingNewsAdapter(this)
        binding.breakingNewsRv.apply {
            adapter = breakingNewsAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        }
        breakingNewsAdapter.setOnItemClickListener(object :
            BreakingNewsAdapter.OnItemClickListener {
            override fun onLikeClicked(position: Int, article: Article) {
                GlobalScope.launch(Dispatchers.IO) {
                    newsViewModel.saveArticle(article)
                    newsViewModel.saveArticleResponse.collect { response ->
                        when (response) {
                            is Resource.Error -> {
                                Log.e(TAG, response.message!!.toString())
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Something wrong happen!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                articleID = response.data!!
                                if (articleID == -1L) {
                                    createSnackBar("Article already saved before!").show()
                                } else {
                                    createSnackBar("Article Saved Successfully.").setAction("Undo") {
                                        createSnackBar("Article removed successfully.").show()
                                        newsViewModel.deleteArticle(articleID)
                                    }.show()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setUpCategoryNewsRv() {
        categoryNewsAdapter = CategoryNewsAdapter(this)
        binding.categoryNewsRv.apply {
            adapter = categoryNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        categoryNewsAdapter.setOnItemClickListener(object :
            CategoryNewsAdapter.OnItemClickListener {
            override fun onLikeClicked(position: Int, article: Article) {
                GlobalScope.launch(Dispatchers.IO) {
                    newsViewModel.saveArticle(article)
                    newsViewModel.saveArticleResponse.collect { response ->
                        when (response) {
                            is Resource.Error -> {
                                Log.e(TAG, response.message!!.toString())
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Something wrong happen!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                articleID = response.data!!
                                if (articleID == -1L) {
                                    createSnackBar("Article already saved before!").show()
                                } else {
                                    createSnackBar("Article Saved Successfully.").setAction("Undo") {
                                        createSnackBar("Article removed successfully.").show()
                                        newsViewModel.deleteArticle(articleID)
                                    }.show()
                                }
                            }
                        }
                    }
                }
            }

        })
    }

    private fun createSnackBar(msg: String): Snackbar {
        return Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            msg,
            Snackbar.LENGTH_SHORT
        )
    }

    companion object {
        private const val TAG = "NewsFragment"
    }
}