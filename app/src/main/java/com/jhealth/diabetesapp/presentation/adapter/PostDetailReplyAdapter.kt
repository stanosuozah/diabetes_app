package com.jhealth.diabetesapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.PostCommentsViewholderBinding
import com.jhealth.diabetesapp.domain.model.Reply

class PostDetailReplyAdapter(private val email:String):  ListAdapter<Reply, PostDetailReplyAdapter.ViewHolder>(
    diffObject
) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = PostCommentsViewholderBinding.bind(view)

        fun bind(reply: Reply) {
            binding.authorText.text = reply.email
            binding.msg.text = reply.replyDescription
            binding.profileImg.setImageResource(R.drawable.dia_cooking)
            binding.profileImg.setOnClickListener {
                listener?.let { it(reply) }
            }
            binding.deleteBtn.isVisible = email.equals(reply.email,true)
            binding.deleteBtn.setOnClickListener {view->
                viewListener?.let { it(view,reply) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.post_comments_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Reply>() {
            override fun areItemsTheSame(
                oldItem: Reply,
                newItem: Reply
            ): Boolean {
                return oldItem.replyId == newItem.replyId
            }

            override fun areContentsTheSame(
                oldItem: Reply,
                newItem: Reply
            ): Boolean {
                return oldItem.replyId == newItem.replyId && oldItem.email == newItem.email
            }
        }
    }

    private var listener: ((Reply) -> Unit)? = null
    fun profileClick(listener: (Reply) -> Unit) {
        this.listener = listener
    }
    private var deleteListener: ((Reply) -> Unit)? = null
    fun deleteClick(listener: (Reply) -> Unit) {
        this.deleteListener = listener
    }
    private var viewListener: ((View, Reply) -> Unit)? = null
    fun viewClick(listener: (View, Reply) -> Unit) {
        this.viewListener = listener
    }
}