package com.jhealth.diabetesapp.domain.model

data class ArticleResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)