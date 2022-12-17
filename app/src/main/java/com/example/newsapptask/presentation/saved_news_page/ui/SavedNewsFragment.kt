package com.example.newsapptask.presentation.saved_news_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapptask.R
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentSavedNewsBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.saved_news_page.adapter.SavedNewsAdapter
import com.example.newsapptask.presentation.saved_news_page.viewmodel.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {

    private lateinit var binding: FragmentSavedNewsBinding
    private  val savedNewsViewModel: SavedNewsViewModel by viewModels()
    private lateinit var savedNewsAdapter: SavedNewsAdapter
    private lateinit var article: Article
    private lateinit var articles: MutableList<Article>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        setUpRecyclerView()

        //getting the saved news from the database.
        getSavedDBArticles()

        //handling right and left swipes on articles for deleting.
        handleSwipeArticleListener()

        return binding.root
    }

    private fun handleSwipeArticleListener() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                article = savedNewsAdapter.differ.currentList[position]
                savedNewsViewModel.deleteArticle(article)
                CoroutineScope(Dispatchers.Main).launch {
                    savedNewsViewModel.deleteArticleResponse.collect { response ->
                        when (response) {
                            is Resource.Error -> Log.e(TAG, response.message!!)
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                // try and catch for not attached Exception.
                                try {
                                    Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        getString(R.string.article_removed_msg),
                                        Snackbar.LENGTH_LONG
                                    ).apply {
                                        setAction(getString(R.string.undo_msg)) {
                                            article.isSaved = true
                                            savedNewsViewModel.saveArticle(article)
                                        }
                                        show()
                                    }
                                } catch (ex: Exception) {
                                    Log.e(TAG, ex.message.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
    }

    private fun getSavedDBArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            savedNewsViewModel.getSavedArticles()
            savedNewsViewModel.getSavedNewsResponse.collect { response ->
                when (response) {
                    is Resource.Error -> {
                        Log.e(TAG, response.message!!)
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            if (response.data!!.isEmpty()){
                                binding.tvNoSavedNewsFound.visibility = View.VISIBLE
                            }else{
                                binding.tvNoSavedNewsFound.visibility = View.GONE
                                articles = (response.data as MutableList<Article>?)!!
                                savedNewsAdapter.differ.submitList(response.data.toList())
                            }
                        }
                    }
                }
            }
        }

    }

    private fun setUpRecyclerView() {
        savedNewsAdapter = SavedNewsAdapter(this)
        binding.rvSavedNews.apply {
            adapter = savedNewsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        private const val TAG = "SavedNewsFragment"
    }
}