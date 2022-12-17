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
import com.example.newsapptask.R
import com.example.newsapptask.common.Constants
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentNewsBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.news_page.adapter.NewsAdapter
import com.example.newsapptask.presentation.news_page.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class NewsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentNewsBinding
    /*
        We have 2 rv for breaking news - category news with different properties,
        so we have 2 objects from the same Adapter and attach each one to its rv.
     */
    private lateinit var breakingNewsAdapter: NewsAdapter
    private lateinit var categoryNewsAdapter: NewsAdapter
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

        //Breaking news articles based on selected country.
        getBreakingNewsArticles()
        //Category news articles based on selected country and category.
        getCategoryNewsArticles()

        binding.ivSortBy.setOnClickListener(this)

        return binding.root
    }

    private fun getCategoryNewsArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            newsViewModel.getCategoryNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                        withContext(Dispatchers.Main) {
                            binding.shimmerEffect.stopShimmer()
                            binding.shimmerEffect.visibility = View.GONE
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.shimmerEffect.stopShimmer()
                            binding.shimmerEffect.visibility = View.GONE
                            binding.categoryNewsRv.visibility = View.VISIBLE
                            categoryNewsAdapter.differ.submitList(changeArticlesViewType(response.data!!))
                        }
                    }
                }
            }
        }
    }

    // changing the articles adapter viewType before sending it to the adapter.
    private fun changeArticlesViewType(data: List<Article>): List<Article> {
        data.forEach { it.viewType = Constants.CATEGORY_NEWS_VIEWTYPE }
        return data
    }

    private fun getBreakingNewsArticles() {
        newsViewModel.articles.observe(viewLifecycleOwner) { response ->
            breakingNewsAdapter.differ.submitList(response.data!!.distinctBy { it.url })
            if (response is Resource.Error)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.offline_mode_info_msg),
                    Toast.LENGTH_SHORT
                )
                    .show()

        }
    }

    private fun setUpBreakingNewsRv() {
        breakingNewsAdapter = NewsAdapter(this)
        binding.breakingNewsRv.apply {
            adapter = breakingNewsAdapter
            layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        }
        breakingNewsAdapter.setOnItemClickListener(object :
            NewsAdapter.OnItemClickListener {
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
                                        getString(R.string.somthing_wrong_error_msg),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                articleID = response.data!!
                                if (articleID == -1L) {
                                    createSnackBar(getString(R.string.article_already_saved_msg)).show()
                                } else {
                                    createSnackBar(getString(R.string.article_saved_msg)).setAction(
                                        getString(R.string.undo_msg)
                                    ) {
                                        createSnackBar(getString(R.string.article_removed_msg)).show()
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
        categoryNewsAdapter = NewsAdapter(this)
        binding.shimmerEffect.startShimmer()
        binding.categoryNewsRv.apply {
            adapter = categoryNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        categoryNewsAdapter.setOnItemClickListener(object :
            NewsAdapter.OnItemClickListener {
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
                                        getString(R.string.somthing_wrong_error_msg),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                articleID = response.data!!
                                if (articleID == -1L) {
                                    createSnackBar(getString(R.string.article_already_saved_msg)).show()
                                } else {
                                    createSnackBar(getString(R.string.article_saved_msg)).setAction(
                                        getString(R.string.undo_msg)
                                    ) {
                                        createSnackBar(getString(R.string.article_removed_msg)).show()
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivSortBy -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.sorted_articles_info_msg),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}