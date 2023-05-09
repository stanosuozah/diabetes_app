package com.jhealth.diabetesapp.domain

import com.jhealth.diabetesapp.data.dto.FullMealResponseDTO
import com.jhealth.diabetesapp.data.dto.MealResponseDTO
import com.jhealth.diabetesapp.domain.model.ArticleResponse
import com.jhealth.diabetesapp.domain.model.Posts
import com.jhealth.diabetesapp.domain.model.Reply
import com.jhealth.diabetesapp.domain.model.User
import com.jhealth.diabetesapp.util.RequestState
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.flow.Flow

interface DiabetesRepository {
    fun signUp(fName:String,lName:String,email:String,password:String): Flow<RequestState>
    fun login(email:String,password:String): Flow<RequestState>
    fun sendVerificationLink(): Flow<RequestState>
    fun sendPost(posts: Posts): Flow<RequestState>
    fun getAllPost(): Flow<Resource<List<Posts>>>
    fun sendReplyToPost(postId: String,reply: Reply): Flow<RequestState>
    fun getRepliesToPost(postId: String): Flow<Resource<List<Reply>>>
    fun deleteReplyToPost(postId: String,reply: Reply): Flow<RequestState>
    fun getCurrentUser(email: String): Flow<Resource<User>>
    fun updateUserInfo(user: User):Flow<RequestState>
    suspend fun getAllMealsPerCategory(category: String):Flow<Resource<MealResponseDTO>>
    suspend fun getMealInfoById(id: String):Resource<FullMealResponseDTO>
    suspend fun getAllNews():Resource<ArticleResponse>
}