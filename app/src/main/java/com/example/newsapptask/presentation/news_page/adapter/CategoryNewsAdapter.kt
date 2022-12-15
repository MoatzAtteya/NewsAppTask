package com.example.newsapptask.presentation.news_page.adapter

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

class CategoryNewsAdapter(private val fragment: NewsFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

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
        val article = differ.currentList[position]
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
        return differ.currentList.size
    }

    private class CategoryNewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CategoryNewsItemBinding.bind(view)
    }
}