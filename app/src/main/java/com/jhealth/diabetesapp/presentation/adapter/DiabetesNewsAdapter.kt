package com.jhealth.diabetesapp.presentation.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FullItemViewholderBinding
import com.jhealth.diabetesapp.domain.model.Article
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class DiabetesNewsAdapter:  ListAdapter<Article, DiabetesNewsAdapter.ViewHolder>(
    diffObject
) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = FullItemViewholderBinding.bind(view)

        fun bind(item: Article) {
            binding.itemTitle.text = item.title
            binding.authorText.text = item.source?.name ?: "Some Author"
            binding.readTimeText.text = item.publishedAt?.let { getFormattedDate(it) }
            binding.itemImage.load(item.urlToImage){
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }
            binding.root.setOnClickListener {
                listener?.let { it(item) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.full_item_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Article,
                newItem: Article
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.title == newItem.title
            }
        }
    }

    private var listener: ((Article) -> Unit)? = null
    fun adapterClick(listener: (Article) -> Unit) {
        this.listener = listener
    }


    private  fun getFormattedDate(date: String):String{
        val format =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val localDate = LocalDateTime.parse(date, format)
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.getDefault())
        return localDate.format(dateFormatter)
    }

}