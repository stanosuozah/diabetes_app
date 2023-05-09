package com.jhealth.diabetesapp.domain.model

data class Recipe(
    val id:Int =0,
    val recipe_title:String="",
    val recipe_author:String="",
    val recipe_category:String="",
    val recipe_image:String="",
    val recipe_video:String="",
    val nutrition_fact:String="",
    val ingredient:String="",
    val cooking_instruction:String="",
)
