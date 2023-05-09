package com.jhealth.diabetesapp.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.data.dto.FullMealResponseDTO
import com.jhealth.diabetesapp.data.dto.MealResponseDTO
import com.jhealth.diabetesapp.domain.DiabetesRepository
import com.jhealth.diabetesapp.domain.model.ArticleResponse
import com.jhealth.diabetesapp.domain.model.Posts
import com.jhealth.diabetesapp.domain.model.Reply
import com.jhealth.diabetesapp.domain.model.User
import com.jhealth.diabetesapp.util.*
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DiabetesRepositoryImpl : DiabetesRepository {
    private val fAuth = Firebase.auth
    private val fStoreUsers = Firebase.firestore.collection(USERS)
    private val postStore = Firebase.firestore.collection(POSTS)

    override fun signUp(
        fName: String,
        lName: String,
        email: String,
        password: String
    ): Flow<RequestState> {
        return callbackFlow {
            try {
                val signUp = fAuth.createUserWithEmailAndPassword(email, password).await()
                val newUser = signUp.user?.uid?.let {
                    User(email = email, password = password, firstName = fName, lastName = lName)
                } ?: Log.d("dia_auth", "Some nullable error occurred at sign up...")
                fStoreUsers.document(email).set(newUser).await()
                trySend(RequestState.Successful(true))
                Log.d("dia_auth", "SUCCESS ALl SIGN UP TRANSACTION COMPLETED")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_auth", "SIGN UP ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun login(email: String, password: String): Flow<RequestState> {
        return callbackFlow {
            try {
                fAuth.signInWithEmailAndPassword(email, password).await()
                trySend(RequestState.Successful(true))
                Log.d("dia_auth", "SUCCESS LOGIN OK")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_auth", "LOGIN ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun sendVerificationLink(): Flow<RequestState> {
        return callbackFlow {
            try {
                fAuth.currentUser?.sendEmailVerification()
                trySend(RequestState.Successful(true))
                Log.d("dia_auth", "SUCCESS LINK SENT")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_auth", "LINK ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun sendPost(posts: Posts): Flow<RequestState> {
        return callbackFlow {
            try {
                postStore.document(posts.postId.toString()).set(posts).await()
                trySend(RequestState.Successful(true))
                Log.d("dia_post", "SUCCESS POST SENT")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_post", "POST ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun sendReplyToPost(postId: String, reply: Reply): Flow<RequestState> {
        return callbackFlow {
            try {
                postStore.document(postId.toString()).collection(POST_REPLIES)
                    .document(reply.replyId.toString()).set(reply).await()
                trySend(RequestState.Successful(true))
                Log.d("dia_reply_to_post", "SUCCESS REPLY SENT")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_reply_to_post", "REPLY ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun deleteReplyToPost(postId: String, reply: Reply): Flow<RequestState> {
        return callbackFlow {
            try {
                postStore.document(postId.toString()).collection(POST_REPLIES)
                    .document(reply.replyId.toString()).delete().await()
                trySend(RequestState.Successful(true))
                Log.d("dia_reply_to_post", "SUCCESS REPLY DELETED")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d("dia_reply_to_post", "REPLY DELETION ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun getAllPost(): Flow<Resource<List<Posts>>> {
        return callbackFlow {
            val allPosts = mutableListOf<Posts>()
            try {
                val posts = postStore.get().await()
                posts.forEach {
                    val post = it.toObject<Posts>()
                    allPosts.add(post)
                }
                trySend(Resource.Successful(allPosts))
                Log.d("dia_fetch_all_post", "SUCCESS GET ALL POST ")
            } catch (e: Exception) {
                trySend(Resource.Failure(e.message.toString()))
                Log.d("dia_fetch_all_post", "GET ALL POST ERROR--->$e")
            }
            awaitClose()
        }

    }

    override fun getRepliesToPost(postId: String): Flow<Resource<List<Reply>>> {
        return callbackFlow {
            val allReplies = mutableListOf<Reply>()
            try {
                val replies =
                    postStore.document(postId.toString()).collection(POST_REPLIES).get().await()
                replies.forEach {
                    val reply = it.toObject<Reply>()
                    allReplies.add(reply)
                }
                trySend(Resource.Successful(allReplies))
                Log.d("dia_fetch_replies_post", "SUCCESS GET ALL REPLIES ")
            } catch (e: Exception) {
                trySend(Resource.Failure(e.message.toString()))
                Log.d("dia_fetch_replies_post", "GET ALL REPLIES ERROR--->$e")
            }
            awaitClose()
        }

    }

    override suspend fun getAllMealsPerCategory(category: String): Flow<Resource<MealResponseDTO>> {
        return callbackFlow {
            trySend(Resource.Loading())
            try {
                val response = ApiService.recipeApiService.getAllMealPerCategory(category)
                Log.d("dia_meals_api_all_meals", "getAllMealsPerCategory: response-> ${response.body()}")
                trySend(Resource.Successful(response.body()))
            } catch (e: Exception) {
                Log.d("dia_meals_api_all_meals", "getAllMealsPerCategory: ERROR-> $e")
                trySend(Resource.Failure(e.message.toString()))
            }
            awaitClose()
        }
    }

    override suspend fun getMealInfoById(id: String): Resource<FullMealResponseDTO> {
        return try {
            val response = ApiService.recipeApiService.getMealInfoById(id)
            Log.d("dia_meals_api_all_meals", "getAllMealById: response-> ${response.body()}")
            Resource.Successful(response.body())
        } catch (e: Exception) {
            Log.d("dia_meals_api_all_meals", "getAllMealById: ERROR-> $e")
            Resource.Failure(e.message.toString())
        }
    }

     override fun getCurrentUser(email: String): Flow<Resource<User>> {
        return callbackFlow {

            try {
                val snapshot =
                    fStoreUsers.document(email).get().await()

                 snapshot.toObject<User>()?.let {
                     trySend(Resource.Successful(it))
                 }?: trySend(Resource.Failure("Unable to get user, NULL"))
                Log.d("dia_get_user", "SUCCESS GET USER ")
            } catch (e: Exception) {
                trySend(Resource.Failure(e.message.toString()))
                Log.d("dia_get_user", "GET USER ERROR--->$e")
            }
            awaitClose()
        }

    }

  override fun updateUserInfo(user: User):Flow<RequestState>{
        return callbackFlow {

            try {
                val userMap = HashMap<String,String>()
                userMap["firstName"] = user.firstName
                userMap["lastName"] = user.lastName

                fStoreUsers.document(user.email).update(userMap.toMap()).await()
                trySend(RequestState.Successful(true))
                Log.d("dia_update_user", "SUCCESS UPDATE USER ")
            } catch (e: Exception) {
                trySend(RequestState.Failure(e.message.toString()))
                Log.d("dia_update_user", "UPDATE USER ERROR--->$e")
            }
            awaitClose()
        }

    }

   override suspend fun getAllNews():Resource<ArticleResponse>{
        return try {
            val response = ApiService.newsApiService.getTrendingNews()
            Log.d("dia_news_api_all_news", "getAllNews: ${response.body()}")
            response.body()?.let {
                Resource.Successful(it)
            }?: Resource.Failure("Some Error Occurred...")
        }catch (e:Exception){
            Log.d("dia_news_api_all_news", "getAllNews: ERROR-> $e")
            Resource.Failure(e.message.toString())
        }
    }

}