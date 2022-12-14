package com.example.newsapptask.presentation.news_page.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapptask.R
import com.example.newsapptask.databinding.BreakingNewsItemBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.news_page.ui.NewsFragment

class BreakingNewsAdapter(private val fragment: NewsFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var isLikeClicked = false


    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BreakingNewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.breaking_news_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article = differ.currentList[position]
        if (holder is BreakingNewsViewHolder) {
            holder.binding.apply {
                Glide.with(fragment.requireContext())
                    .load(article.urlToImage).error(R.drawable.breaking_news_img)
                    .into(ivArticleImage)

                tvSource.text = article.source?.name
                tvDescription.text = article.title
                tvPublishedAt.text = article.publishedAt

                shareIv.setOnClickListener {
                    val intent = Intent().apply {
                        type = "text/plain"
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT , article.url)
                    }
                    val shareIntent = Intent.createChooser(intent , "Share Article")
                    fragment.requireContext().startActivity(shareIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private class BreakingNewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = BreakingNewsItemBinding.bind(view)
    }
}