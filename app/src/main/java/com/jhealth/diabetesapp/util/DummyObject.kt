package com.jhealth.diabetesapp.util

import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.presentation.adapter.Category
import com.jhealth.diabetesapp.domain.model.ItemModel


object DummyObject {

    fun getData(): ArrayList<ItemModel> {
        val items = ArrayList<ItemModel>()
        return items.apply {
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Sports"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Entertainment"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Gossip"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Irrelevant"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Tech"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Fashion"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Business"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Politics"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Health"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Politics"
                )
            )
            add(
                ItemModel(
                    R.drawable.dia_cooking,
                    "Tech"
                )
            )
        }
    }

    fun getCategories() = listOf(
        Category("Beef"),
        Category("Breakfast"),
        Category("Chicken"),
        Category("Dessert"),
        Category("Goat"),
        Category("Lamb"),
        Category("Miscellaneous"),
        Category("Pasta"),
        Category("Pork"),
        Category("Seafood"),
        Category("Side"),
        Category("Starter"),
        Category("Vegan"),
        Category("Vegetarian")
    )
    fun getDefaultCategories() = listOf(
        Category("Beef",true),
        Category("Breakfast"),
        Category("Chicken"),
        Category("Dessert"),
        Category("Goat"),
        Category("Lamb"),
        Category("Miscellaneous"),
        Category("Pasta"),
        Category("Pork"),
        Category("Seafood"),
        Category("Side"),
        Category("Starter"),
        Category("Vegan"),
        Category("Vegetarian")
    )

}

