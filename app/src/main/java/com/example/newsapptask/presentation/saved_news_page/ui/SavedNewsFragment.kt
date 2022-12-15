package com.example.newsapptask.presentation.saved_news_page.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapptask.common.Resource
import com.example.newsapptask.databinding.FragmentSavedNewsBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.saved_news_page.adapter.SavedNewsAdapter
import com.example.newsapptask.presentation.saved_news_page.viewmodel.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {

    private lateinit var binding: FragmentSavedNewsBinding
    private lateinit var savedNewsViewModel: SavedNewsViewModel
    private lateinit var savedNewsAdapter: SavedNewsAdapter
    private lateinit var article: Article
    private lateinit var articles: MutableList<Article>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        savedNewsViewModel =
            ViewModelProvider(this).get(SavedNewsViewModel::class.java)

        binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
        setUpRecyclerView()

        GlobalScope.launch(Dispatchers.IO) {
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
                                savedNewsAdapter.differ.submitList(response.data!!.toList())
                            }
                        }
                    }
                }
            }
        }

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
                GlobalScope.launch(Dispatchers.Main) {
                    savedNewsViewModel.deleteArticleResponse.collect { response ->
                        when (response) {
                            is Resource.Error -> Log.e(TAG, response.message!!)
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                try {
                                    Snackbar.make(
                                        requireActivity().findViewById(android.R.id.content),
                                        "Successfully deleted article",
                                        Snackbar.LENGTH_LONG
                                    ).apply {
                                        setAction("Undo") {
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

        return binding.root
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