package com.jhealth.diabetesapp.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.CategoryItemViewholderBinding
import com.jhealth.diabetesapp.domain.model.Posts

class HomePostsItemAdapter :  ListAdapter<Posts, HomePostsItemAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CategoryItemViewholderBinding.bind(view)

        fun bind(item: Posts) {
            binding.titleText.text = item.postTitle
            binding.itemImg.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.article_icon))
            val tintList =
                ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.yellow_green))
            binding.itemImg.imageTintList = tintList
            binding.root.setOnClickListener {
                listener?.let { it(item) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Posts>() {
            override fun areItemsTheSame(
                oldItem: Posts,
                newItem: Posts
            ): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(
                oldItem: Posts,
                newItem: Posts
            ): Boolean {
                return oldItem.postId == newItem.postId && oldItem.postTitle == newItem.postTitle
            }
        }
    }

    private var listener: ((Posts) -> Unit)? = null
    fun adapterClick(listener: (Posts) -> Unit) {
        this.listener = listener
    }
}