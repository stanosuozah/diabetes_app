package com.jhealth.diabetesapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FullItemViewholderBinding
import com.jhealth.diabetesapp.domain.model.Recipe

class FullRecipesAdapter : ListAdapter<Recipe, FullRecipesAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = FullItemViewholderBinding.bind(view)

        fun bind(item: Recipe) {
            binding.itemTitle.text = item.recipe_title
            binding.authorText.text = item.recipe_author
            binding.readTimeText.text = ""

            binding.itemImage.load(item.recipe_image){
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
            LayoutInflater.from(parent.context)
                .inflate(R.layout.full_item_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(
                oldItem: Recipe,
                newItem: Recipe
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Recipe,
                newItem: Recipe
            ): Boolean {
                return oldItem.id == newItem.id && oldItem.recipe_title == newItem.recipe_title
            }
        }
    }

    private var listener: ((Recipe) -> Unit)? = null
    fun adapterClick(listener: (Recipe) -> Unit) {
        this.listener = listener
    }
}