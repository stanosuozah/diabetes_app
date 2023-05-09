package com.jhealth.diabetesapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.ForumPostsViewholderBinding
import com.jhealth.diabetesapp.domain.model.Posts
import com.jhealth.diabetesapp.util.posts

class PostsAdapter:  ListAdapter<Posts, PostsAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ForumPostsViewholderBinding.bind(view)

        fun bind(post: Posts) {
            binding.postTitle.text = post.postTitle
            binding.postContentText.text = post.postDescription
            binding.authorText.text = post.email
            binding.root.setOnClickListener {
                listener?.let { it(post) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.forum_posts_viewholder, parent, false)
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
                return oldItem.postId == newItem.postId && oldItem.email == newItem.email
            }
        }
    }

    private var listener: ((Posts) -> Unit)? = null
    fun adapterClick(listener: (Posts) -> Unit) {
        this.listener = listener
    }
}