package com.jhealth.diabetesapp.domain

import com.jhealth.diabetesapp.data.dto.FullMealResponseDTO
import com.jhealth.diabetesapp.data.dto.MealResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {
    @GET("filter.php")
    suspend fun getAllMealPerCategory(
        @Query("c")
        category: String
    ): Response<MealResponseDTO>

    @GET("lookup.php")
    suspend fun getMealInfoById(
        @Query("i")
        id: String
    ): Response<FullMealResponseDTO>
}