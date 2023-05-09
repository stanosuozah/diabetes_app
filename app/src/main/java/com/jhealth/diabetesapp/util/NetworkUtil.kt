package com.jhealth.diabetesapp.util

import com.jhealth.diabetesapp.domain.MealDbApi
import com.jhealth.diabetesapp.domain.NewsApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


private const val MEAL_BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
private const val NEWS_BASE_URL = "https://newsapi.org/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val diabetesAppRetrofit = Retrofit.Builder()
    .client(getOkHttp())
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(MEAL_BASE_URL)
    .build()

private val diabetesNewsRetrofit = Retrofit.Builder()
    .client(getOkHttp())
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(NEWS_BASE_URL)
    .build()


object ApiService {
    val recipeApiService: MealDbApi by lazy {
        diabetesAppRetrofit.create(MealDbApi::class.java)
    }

 val newsApiService: NewsApiService by lazy {
        diabetesNewsRetrofit.create(NewsApiService::class.java)
    }

}

private fun getOkHttp(): OkHttpClient {
    val logger = HttpLoggingInterceptor()
    logger.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder().addInterceptor(logger).build()
}