package com.example.newsapptask.presentation.search_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapptask.common.Constants
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentSearchBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.saved_news_page.ui.SavedNewsFragment
import com.example.newsapptask.presentation.search_page.adapter.SearchedNewsAdapter
import com.example.newsapptask.presentation.search_page.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchNewsAdapter: SearchedNewsAdapter
    private var articleID: Long = 0
    private var isFilterClicked = false
    private lateinit var favouriteCategories: MutableSet<String>
    private var searchCategory = mutableSetOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        favouriteCategories = searchViewModel.getPreferencesCategories()!!
        setUpSearchNewsRv()
        setUpCategorySpinner()

        binding.svNewsSearch.setOnQueryTextListener(this)

        binding.tvChangeSearchCategory.setOnClickListener {
            if (!isFilterClicked) {
                binding.categorySpinner.visibility = View.VISIBLE
                binding.tvResetSearchCategory.visibility = View.VISIBLE
                isFilterClicked = true
            } else {
                binding.categorySpinner.visibility = View.GONE
                binding.tvResetSearchCategory.visibility = View.GONE
                isFilterClicked = false

            }
        }

        binding.tvResetSearchCategory.setOnClickListener {
            searchCategory.clear()
            favouriteCategories = searchViewModel.getPreferencesCategories()!!
            Toast.makeText(requireContext(), "Search category reset.", Toast.LENGTH_SHORT)
                .show()
        }

        return binding.root
    }

    private fun setUpCategorySpinner() {
        binding.categorySpinner.apply {
            setItems(Constants.categories)
            setOnItemSelectedListener { view, position, id, item ->
                searchCategory.clear()
                searchCategory.add(item.toString())
                Toast.makeText(requireContext(), "Search category set to $item", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        GlobalScope.launch(Dispatchers.IO) {
            if (searchCategory.isNotEmpty()) {
                searchViewModel.getAndReturnNews(searchCategory, query!!)
            } else {
                searchViewModel.getAndReturnNews(favouriteCategories, query!!)
            }
            searchViewModel.getNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            Log.e(TAG, response.message!!)
                            Toast.makeText(
                                requireContext(),
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.progressBar.visibility = View.GONE
                            if (response.data!!.isEmpty()) {
                                binding.tvNoResultFound.visibility = View.VISIBLE
                            } else {
                                binding.tvNoResultFound.visibility = View.GONE
                                searchNewsAdapter.setArticlesList(mutableListOf())
                                searchNewsAdapter.setArticlesList(response.data as MutableList<Article>)
                                searchNewsAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun setUpSearchNewsRv() {
        searchNewsAdapter = SearchedNewsAdapter(this)
        binding.rvSearchedNews.apply {
            adapter = searchNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        searchNewsAdapter.setOnItemClickListener(object : SearchedNewsAdapter.OnItemClickListener {
            override fun onLikeClicked(position: Int, article: Article) {
                GlobalScope.launch(Dispatchers.IO) {
                    searchViewModel.saveArticle(article)
                    searchViewModel.saveArticleResponse.collect { response ->
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
                                    // try and catch for not attached Exception.
                                    try {
                                        createSnackBar("Article already saved before!").show()
                                    } catch (ex: Exception) {
                                        Log.e(TAG, ex.message.toString())
                                    }
                                } else {
                                    // try and catch for not attached Exception.
                                    try {
                                        createSnackBar("Article Saved Successfully.").setAction("Undo") {
                                            createSnackBar("Article removed successfully.").show()
                                            article.id = articleID.toInt()
                                            searchViewModel.deleteArticle(article)
                                        }.show()
                                    } catch (ex: Exception) {
                                        Log.e(TAG, ex.message.toString())
                                    }
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
        private const val TAG = "SearchFragment"
    }
}