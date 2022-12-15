package com.example.newsapptask.presentation.first_time_page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapptask.R
import com.example.newsapptask.databinding.BreakingNewsItemBinding
import com.example.newsapptask.databinding.CountrySelectItemBinding
import com.example.newsapptask.domain.model.Article
import com.example.newsapptask.domain.model.Country

class CountryAdapter(private val context: Context, private val countries: ArrayList<Country>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.country_select_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val country = countries[position]
        if (holder is CountryViewHolder) {
            holder.binding.ivSelectCountry.setImageDrawable(context.resources.getDrawable(country.image))
            holder.itemView.setOnClickListener {
                mListener.onCountryClicked(position , country)
            }
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    interface OnItemClickListener {
        fun onCountryClicked(position: Int, country: Country)
    }

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CountrySelectItemBinding.bind(view)
    }

}