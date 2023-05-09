package com.jhealth.diabetesapp.data.dto

import com.google.gson.annotations.SerializedName
import com.jhealth.diabetesapp.domain.model.Recipe

data class MealResponseDTO(
    @SerializedName("meals")
    val meals: List<MealDTO>
){
    fun toRecipeList():List<Recipe>{
        return meals.map {
            it.toRecipe()
        }
    }
}
