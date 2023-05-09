package com.jhealth.diabetesapp.presentation.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.CategoriesViewholderBinding

class CategoriesAdapter() : ListAdapter<Category, CategoriesAdapter.ViewHolder>(diffObject) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CategoriesViewholderBinding.bind(view)
        private val typedValue = TypedValue()
        private val theme = itemView.context.theme
        fun bind(article: Category) {
            binding.apply {
                categoryText.text = article.title
                if (article.isSelected) {
                   /* val tintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.purple_200))
                    root.backgroundTintList = tintList*/
                    theme.resolveAttribute(
                        com.google.android.material.R.attr.colorSurface,
                        typedValue,
                        true
                    )
                    val color = typedValue.data
                    categoryText.setTextColor(color)
                    root.background = ContextCompat.getDrawable(itemView.context, R.drawable.categories_background_filled)
                } else {

                    categoryText.setTextColor(ContextCompat.getColor(itemView.context,R.color.yellow_green))

                 /*   val tintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,android.R.color.transparent))
                    root.backgroundTintList = tintList*/
                    root.background = ContextCompat.getDrawable(itemView.context,R.drawable.categories_background)

                }
                root.setOnClickListener {
                    listener?.let {
                        it(article)
                    }
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.categories_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = getItem(position)
        if (pos != null)
            holder.bind(pos)

    }

    companion object {
        val diffObject = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.title == newItem.title && oldItem.isSelected == newItem.isSelected
            }
        }
    }

    private var listener: ((Category) -> Unit)? = null

    fun adapterClickListener(listener: (Category) -> Unit) {
        this.listener = listener
    }


}
