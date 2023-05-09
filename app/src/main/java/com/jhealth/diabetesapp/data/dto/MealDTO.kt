package com.jhealth.diabetesapp.data.dto

import com.google.gson.annotations.SerializedName
import com.jhealth.diabetesapp.domain.model.Recipe

data class MealDTO(
    @SerializedName("strMeal")
    val mealTitle: String,
    @SerializedName("strMealThumb")
    val mealImg: String,
    @SerializedName("idMeal")
    val mealId: String,
) {
    fun toRecipe(): Recipe {

        return Recipe(
            id = mealId.toInt(),
            recipe_title = mealTitle,
            recipe_author = "TheMealDB.com",
            recipe_image = mealImg,
        )
    }
}