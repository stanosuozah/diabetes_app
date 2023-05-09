package com.jhealth.diabetesapp.presentation.arch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.data.DiabetesRepositoryImpl
import com.jhealth.diabetesapp.data.dto.FullMealResponseDTO
import com.jhealth.diabetesapp.data.dto.MealResponseDTO
import com.jhealth.diabetesapp.domain.model.*
import com.jhealth.diabetesapp.presentation.adapter.Category
import com.jhealth.diabetesapp.util.DummyObject
import com.jhealth.diabetesapp.util.POSTS
import com.jhealth.diabetesapp.util.POST_REPLIES
import com.jhealth.diabetesapp.util.RequestState
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DiabetesViewModel : ViewModel() {
    private val repository = DiabetesRepositoryImpl()
    private val postStore =
        Firebase.firestore.collection(POSTS)

    var signUpState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set
    var loginState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set

    var authState = MutableStateFlow(true)
        private set

    var sendPostState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set
    var replyPostState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set
    var allPostState = MutableStateFlow<Resource<List<Posts>>>(Resource.Loading())
        private set
    var allRepliesState = MutableStateFlow<Resource<List<Reply>>>(Resource.Loading())
        private set
    var selectedCategoriesFlow =
        MutableStateFlow<List<Category>>(DummyObject.getDefaultCategories())
        private set
    private val categoryQueryFlow = MutableStateFlow("Beef")

    var allMealsStateFlow = MutableStateFlow<Resource<MealResponseDTO>>(Resource.Loading())
        private set

    var currentMealState = MutableStateFlow<Resource<FullMealResponseDTO>>(Resource.Loading())
        private set

    var firstLaunch = MutableStateFlow(true)
        private set

    var showEllipse = MutableStateFlow(true)
        private set

    var postSortOrder = MutableStateFlow("Descending")
        private set

    var deleteReplyState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set

    var searchFlowCategory = MutableStateFlow("Recipe")
        private set

    var searchPostState =
        MutableStateFlow<Resource<List<Posts>>>(Resource.Failure("Search Post ..."))
        private set

    var searchRecipeState =
        MutableStateFlow<Resource<MealResponseDTO>>(Resource.Failure("Search Recipe ..."))
        private set

    var currentUser =
        MutableStateFlow<Resource<User>>(Resource.Loading())
        private set

    var currentUserUpdateState =
        MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set
    var emailVerificationState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set

    var allArticlesStateFlow = MutableStateFlow<Resource<ArticleResponse>>(Resource.Loading())
        private set

    var searchArticleState =
        MutableStateFlow<Resource<List<Article>>>(Resource.Failure("Search Article ..."))
        private set

    fun signUp(fName: String, lName: String, email: String, password: String) {
        signUpState.value = RequestState.Loading
        viewModelScope.launch {
            repository.signUp(fName, lName, email, password).collect {
                signUpState.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        loginState.value = RequestState.Loading
        viewModelScope.launch {
            repository.login(email, password).collect {
                loginState.value = it
            }

        }
    }

    fun toggleAuthState(state: Boolean) {
        authState.value = state
    }

    fun toggleSignUpState(state: RequestState){
        signUpState.value = state
    }

    fun toggleLoginState(state: RequestState){
        loginState.value = state
    }

    fun sendPost(posts: Posts) {
        sendPostState.value = RequestState.Loading
        viewModelScope.launch {
            repository.sendPost(posts).collect {
                sendPostState.value = it
            }

        }
    }

    fun replyPost(postId: String, reply: Reply) {
        replyPostState.value = RequestState.Loading
        viewModelScope.launch {
            repository.sendReplyToPost(postId, reply).collect {
                replyPostState.value = it
            }

        }
    }

    fun loadAllPost() {
        viewModelScope.launch {
            repository.getAllPost().collect {
                when (it) {
                    is Resource.Successful -> {
                        it.data?.let { posts ->
                            if (posts.isEmpty()) {
                                allPostState.value = Resource.Failure("No posts available...")
                                return@collect
                            }
                            allPostState.value = Resource.Successful(posts)
                        }
                    }
                    is Resource.Failure -> {
                        allPostState.value = Resource.Failure(it.msg)
                    }
                    else -> Unit
                }
            }

        }
    }

    fun addAllPostSnapshotListener() {
        postStore.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("dia_all_post_listener", "Error---->> $error")
                return@addSnapshotListener
            }
            Log.d("dia_all_post_listener", "listener success---->> $error")
            loadAllPost()
        }
    }

    private fun loadAllReplies(postId: String) {
        viewModelScope.launch {
            repository.getRepliesToPost(postId).collect {
                when (it) {
                    is Resource.Successful -> {
                        it.data?.let { replies ->
                            if (replies.isEmpty()) {
                                allRepliesState.value = Resource.Failure("No replies available...")
                                return@collect
                            }
                            allRepliesState.value = Resource.Successful(replies)
                        }
                    }
                    is Resource.Failure -> {
                        allRepliesState.value = Resource.Failure(it.msg)
                    }
                    else -> Unit
                }
            }

        }
    }

    fun addAllRepliesSnapshotListener(postId: String) {
        postStore.document(postId.toString()).collection(POST_REPLIES)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("dia_all_post_listener", "Error---->> $error")
                    return@addSnapshotListener
                }
                Log.d("dia_all_post_listener", "listener success---->> $error")
                loadAllReplies(postId)
            }
    }

    fun toggleRepliesState(state: Resource<List<Reply>>) {
        allRepliesState.value = state
    }

    fun onCategorySelected(category: Category) {
        val newList = mutableListOf<Category>()
        DummyObject.getCategories().forEach {
            newList.add(Category(it.title, it.title == category.title))
        }
        selectedCategoriesFlow.value = newList

    }

    fun setCategoryQuery(query: String) {
        categoryQueryFlow.value = query
    }

    fun getMealById(id: String) {
        currentMealState.value = Resource.Loading()
        viewModelScope.launch {
            currentMealState.value = repository.getMealInfoById(id)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadAllMeal() {
        var oneFlow: Flow<Resource<MealResponseDTO>> = flowOf()
        viewModelScope.launch {
            oneFlow = categoryQueryFlow.flatMapLatest { category ->
                repository.getAllMealsPerCategory(category)
            }

            oneFlow.collect {
                allMealsStateFlow.value = it
            }
        }
    }

    fun togglePostSortOrder(order: String) {
        postSortOrder.value = order
    }

    fun toggleFirstLaunch(value: Boolean) {
        firstLaunch.value = value
    }

    fun toggleShowEllipse(value: Boolean) {
        showEllipse.value = value
    }


    fun deleteReplyToPost(postId: String, reply: Reply) {
        viewModelScope.launch {
            repository.deleteReplyToPost(postId, reply).collect {
                deleteReplyState.value = it
            }
        }

    }


    fun searchPost(query: String) {
        if (allPostState.value is Resource.Successful) {
            val posts = allPostState.value as Resource.Successful
            val searchResult = posts.data?.filter { it.postTitle.contains(query, true) }
            searchResult?.let {
                if (it.isEmpty()) {
                    searchPostState.value = Resource.Failure("No posts found ...")
                    return
                }
                searchPostState.value = Resource.Successful(it)
            } ?: toggleSearchPostState(Resource.Failure("Nullable error in search post..."))
            return
        }
        searchPostState.value =
            Resource.Failure("No posts found...items have not been loaded")
    }

    fun toggleSearchPostState(state: Resource<List<Posts>>) {
        searchPostState.value = state
    }

    fun searchRecipe(query: String) {
        if (allMealsStateFlow.value is Resource.Successful) {
            val posts = allMealsStateFlow.value as Resource.Successful
            val searchResult = posts.data?.meals?.filter { it.mealTitle.contains(query, true) }
            searchResult?.let {
                if (it.isEmpty()) {
                    searchRecipeState.value = Resource.Failure("No recipe found ...")
                    return
                }
                searchRecipeState.value = Resource.Successful(MealResponseDTO(it))
            } ?: toggleSearchRecipeState(Resource.Failure("Nullable error in search recipe..."))
            return
        }
        searchRecipeState.value =
            Resource.Failure("No recipe found...items have not been loaded ")
    }

    fun toggleSearchRecipeState(state: Resource<MealResponseDTO>) {
        searchRecipeState.value = state
    }

    fun toggleSearchCategoryState(category: String) {
        searchFlowCategory.value = category
    }

    fun updateCurrentUser() {
        currentUser.value = Resource.Successful(User())
    }

 fun updateCurrentUserInfo(user: User) {
     currentUserUpdateState.value = RequestState.Loading
     viewModelScope.launch {
         repository.updateUserInfo(user).collect{
             currentUserUpdateState.value = it
         }

     }

    }

    fun getCurrentUser(email: String){
        currentUser.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentUser(email).collect{
                currentUser.value = it
            }
        }
    }

    fun toggleUpdateProfileState(state: RequestState){
        currentUserUpdateState.value = state
    }

    fun resetSomeNecessaryStates(){
        categoryQueryFlow.value = "Beef"
        selectedCategoriesFlow.value = DummyObject.getDefaultCategories()
        firstLaunch.value = true
        postSortOrder.value = "Descending"
        searchFlowCategory.value = "Recipe"
        loginState.value = RequestState.NonExistent
        signUpState.value = RequestState.NonExistent
    }

    fun sendEmailVerification(){
        emailVerificationState.value = RequestState.Loading
        viewModelScope.launch {
            repository.sendVerificationLink().collect{
                emailVerificationState.value = it
            }
        }
    }

    fun loadTrendingNews(){
        allArticlesStateFlow.value = Resource.Loading()
        viewModelScope.launch {
            allArticlesStateFlow.value = repository.getAllNews()
        }
    }


    fun searchArticle(query: String) {
        if (allArticlesStateFlow.value is Resource.Successful) {
            val articles = allArticlesStateFlow.value as Resource.Successful
            val searchResult = articles.data?.articles?.let { list ->
                list.filter { article ->
                    article.title?.contains(query, true) ?: true
                }
            }
            searchResult?.let {
                if (it.isEmpty()) {
                    searchArticleState.value = Resource.Failure("No articles found ...")
                    return
                }
                searchArticleState.value = Resource.Successful(it)
            } ?: toggleSearchArticleState(Resource.Failure("Nullable error in search article..."))
            return
        }
        searchArticleState.value =
            Resource.Failure("No articles found...items have not been loaded")
    }

    fun toggleSearchArticleState(state: Resource<List<Article>>) {
        searchArticleState.value = state
    }

}