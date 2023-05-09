package com.jhealth.diabetesapp.data.dto


import com.google.gson.annotations.SerializedName

data class FullMealResponseDTO(
    @SerializedName("meals")
    val meals: List<FullMealDTO>
)