package com.jhealth.diabetesapp.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val USERS = "Users"
const val POSTS = "Posts"
const val POST_REPLIES = "Post_replies"
const val API_KEY = "6953a7421bb749c589e687746c85eee8"

val posts = Firebase.firestore.collection(POSTS).document("post 1").collection(POST_REPLIES).document("reply 1")
val eg = Firebase.firestore.collection(POSTS)

fun String.isNotEmptyAction(action: (String) -> Unit) {
    if (this.trim().isNotEmpty()) {
        action(this)
        return
    }
}
fun String?.isNotNullOrEmptyAction(action: (String) -> Unit) {
    this?.let {
        if (it.isNotEmpty()){
            action(it)
        }
    }
}