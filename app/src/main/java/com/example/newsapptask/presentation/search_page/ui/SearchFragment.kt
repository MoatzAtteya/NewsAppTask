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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapptask.R
import com.example.newsapptask.common.Constants
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentSearchBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.search_page.adapter.SearchedNewsAdapter
import com.example.newsapptask.presentation.search_page.viewmodel.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener {

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

        binding.tvChangeSearchCategory.setOnClickListener(this)
        binding.tvResetSearchCategory.setOnClickListener(this)


        return binding.root
    }

    private fun setUpCategorySpinner() {
        binding.categorySpinner.apply {
            setItems(Constants.categories)
            setOnItemSelectedListener { view, position, id, item ->
                searchCategory.clear()
                searchCategory.add(item.toString())
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_category_set_msg, item.toString()),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
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
                CoroutineScope(Dispatchers.IO).launch {
                    searchViewModel.saveArticle(article)
                    searchViewModel.saveArticleResponse.collect { response ->
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
                                    // try and catch for not attached Exception.
                                    try {
                                        createSnackBar(getString(R.string.article_already_saved_msg)).show()
                                    } catch (ex: Exception) {
                                        Log.e(TAG, ex.message.toString())
                                    }
                                } else {
                                    // try and catch for not attached Exception.
                                    try {
                                        createSnackBar(getString(R.string.article_saved_msg)).setAction(
                                            getString(R.string.undo_msg)
                                        ) {
                                            createSnackBar(getString(R.string.article_removed_msg)).show()
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

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tvChangeSearchCategory->{
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
            R.id.tvResetSearchCategory->{
                searchCategory.clear()
                favouriteCategories = searchViewModel.getPreferencesCategories()!!
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_category_reset_msg),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}