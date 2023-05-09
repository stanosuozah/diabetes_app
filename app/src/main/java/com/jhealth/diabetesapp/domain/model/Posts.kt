package com.jhealth.diabetesapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Posts(
    val postId:String="",
    val email:String="",
    val postDescription:String="",
    val postTitle:String=""
):Parcelable
