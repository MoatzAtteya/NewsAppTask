package com.example.newsapptask.presentation.search_page.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapptask.R
import com.example.newsapptask.databinding.CategoryNewsItemBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.presentation.news_page.ui.NewsFragment
import com.example.newsapptask.presentation.search_page.ui.SearchFragment

class SearchedNewsAdapter(private val fragment: SearchFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articles = mutableListOf<Article>()

    interface OnItemClickListener {
        fun onLikeClicked(position: Int, article: Article)

    }

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoryNewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_news_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val article = articles[position]
        if (holder is CategoryNewsViewHolder) {
            holder.itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                fragment.startActivity(intent)
            }
            holder.binding.apply {
                Glide.with(fragment.requireContext())
                    .load(article.urlToImage).error(R.drawable.breaking_news_img)
                    .into(ivArticleImage)

                tvArticleTitle.text = article.title
                tvAuthor.text = article.source?.name
                tvDescription.text = article.title
                tvDatePublished.text = article.publishedAt?.replace("T", " ")?.replace("Z"," ")

                likeIv.setOnClickListener {
                    mListener.onLikeClicked(position, article)
                }
                shareIv.setOnClickListener {
                    val intent = Intent().apply {
                        type = "text/plain"
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, article.url)
                    }
                    val shareIntent = Intent.createChooser(intent, "Share Article")
                    fragment.requireContext().startActivity(shareIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setArticlesList(articles : MutableList<Article>){
        this.articles = articles
    }

    private class CategoryNewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CategoryNewsItemBinding.bind(view)
    }
}