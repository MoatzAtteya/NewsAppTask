package com.example.newsapptask.presentation.news_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentNewsBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.news_page.adapter.BreakingNewsAdapter
import com.example.newsapptask.presentation.news_page.adapter.CategoryNewsAdapter
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private lateinit var binding: FragmentNewsBinding
    private lateinit var breakingNewsAdapter: BreakingNewsAdapter
    private lateinit var categoryNewsAdapter: CategoryNewsAdapter
    private val newsViewModel: NewsViewModel by viewModels()
    private var articleID: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsBinding.inflate(inflater, container, false)

        setUpBreakingNewsRv()
        setUpCategoryNewsRv()

        newsViewModel.articles.observe(viewLifecycleOwner) { response ->
            breakingNewsAdapter.differ.submitList(response.data!!)
            if (response is Resource.Error)
                Toast.makeText(
                    requireContext(),
                    "offline mode, Showing cached data.",
                    Toast.LENGTH_SHORT
                )
                    .show()

        }

        CoroutineScope(Dispatchers.IO).launch {
            newsViewModel.getCategoryNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main) {
                            binding.shimmerEffect.stopShimmer()
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Log.d(TAG, "category news are: ${response.data!!.size}")
                        withContext(Dispatchers.Main) {
                            delay(1000)
                            binding.shimmerEffect.stopShimmer()
                            binding.categoryNewsRv.visibility = View.VISIBLE
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
                CoroutineScope(Dispatchers.IO).launch {
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
                                        article.id = articleID.toInt()
                                        newsViewModel.deleteArticle(article)
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
        binding.shimmerEffect.startShimmer()
        binding.categoryNewsRv.apply {
            adapter = categoryNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        categoryNewsAdapter.setOnItemClickListener(object :
            CategoryNewsAdapter.OnItemClickListener {
            override fun onLikeClicked(position: Int, article: Article) {
                CoroutineScope(Dispatchers.IO).launch {
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
                                        article.id = articleID.toInt()
                                        newsViewModel.deleteArticle(article)
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