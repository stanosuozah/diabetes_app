package com.jhealth.diabetesapp.domain

import com.jhealth.diabetesapp.domain.model.ArticleResponse
import com.jhealth.diabetesapp.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/everything")
    suspend fun getTrendingNews(
        @Query("q") query: String ="diabetes",
        @Query("page") page: Int = (1..10).random(),
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<ArticleResponse>
}