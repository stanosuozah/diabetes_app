package com.jhealth.diabetesapp.presentation

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentSearchBinding
import com.jhealth.diabetesapp.presentation.adapter.DiabetesNewsAdapter
import com.jhealth.diabetesapp.presentation.adapter.FullRecipesAdapter
import com.jhealth.diabetesapp.presentation.adapter.PostsAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val postAdapter by lazy { PostsAdapter() }
    private val articleAdapter by lazy { DiabetesNewsAdapter() }
    private val recipeAdapter by lazy { FullRecipesAdapter() }
    private val viewModel by activityViewModels<DiabetesViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
            postRecyclerView.adapter = postAdapter
            recipeRecyclerView.adapter = recipeAdapter
            articleRecyclerView.adapter = articleAdapter
        }
        adaptiveSearchView()
        changeSearchViewPlate()
        setUpSpinner()
        setUpSearchListener()
        observeCategory()
    }


    private fun setUpSpinner() {
        binding.typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                viewModel.toggleSearchCategoryState(binding.typeSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun changeSearchViewPlate() {
        val searchPlate = binding.searchBar.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundResource(R.drawable.transparent_background)
    }

    private fun adaptiveSearchView() {
        val searchEditText =
            binding.searchBar.findViewById(androidx.appcompat.R.id.search_src_text) as EditText

        val searchIcon =
            binding.searchBar.findViewById(androidx.appcompat.R.id.search_mag_icon) as ImageView
        searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.forest_green))
        searchEditText.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.forest_green
            )
        )
        val tintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.forest_green))
        searchIcon.imageTintList = tintList


    }

    private fun setUpSearchListener() {
        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lifecycleScope.launch {
                    viewModel.searchFlowCategory.collect { category ->
                        when(category){
                            "Post"->{
                                query?.let {
                                    viewModel.searchPost(it.trim())
                                    Log.d("dia_search_post", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchPostState(Resource.Failure("Search Posts..."))
                            }
                            "Article"->{
                                query?.let {
                                    viewModel.searchArticle(it.trim())
                                    Log.d("dia_search_article", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchArticleState(Resource.Failure("Search Article..."))
                            }
                            "Recipe"->{
                                query?.let {
                                    viewModel.searchRecipe(it.trim())
                                    Log.d("dia_search_recipe", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchRecipeState(Resource.Failure("Search Recipe..."))
                            }
                        }
                    }
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    viewModel.searchFlowCategory.collect { category ->
                        when(category){
                            "Post"->{
                                newText?.let {
                                    if (newText.trim().isEmpty()){
                                        viewModel.toggleSearchPostState(Resource.Failure("Search Posts..."))
                                        return@collect
                                    }
                                    viewModel.searchPost(it.trim())
                                    Log.d("dia_search_post", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchPostState(Resource.Failure("Search Posts..."))

                            }
                            "Article"->{
                                newText?.let {
                                    if (newText.trim().isEmpty()){
                                        viewModel.toggleSearchArticleState(Resource.Failure("Search Article..."))
                                        return@collect
                                    }
                                    viewModel.searchArticle(it.trim())
                                    Log.d("dia_search_article", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchArticleState(Resource.Failure("Search Article..."))
                            }
                            "Recipe"->{
                                newText?.let {
                                    if (newText.trim().isEmpty()){
                                        viewModel.toggleSearchRecipeState(Resource.Failure("Search Recipe..."))
                                        return@collect
                                    }
                                    viewModel.searchRecipe(it.trim())
                                    Log.d("dia_search_recipe", "SearchQuery: $it")
                                } ?: viewModel.toggleSearchRecipeState(Resource.Failure("Search Recipe..."))
                            }
                        }
                    }
                }

                return false
            }
        })

    }

    private fun observePostSearch() {
        binding.recipeRecyclerView.isVisible = false
        lifecycleScope.launch {
            viewModel.searchPostState.collect {
                when (it) {
                    is Resource.Successful -> {
                        binding.postRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                        postAdapter.submitList(it.data)
                        Log.d("dia_post_search", "observePostSearch:Success-> ${it.data}")
                    }
                    is Resource.Failure -> {
                        binding.postRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = it.msg
                        binding.progressBar.isVisible = false
                        Log.d("dia_post_search", "observePostSearch:Failure")
                    }
                    is Resource.Loading -> {
                        binding.postRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true
                        Log.d("dia_post_search", "observePostSearch:Loading")

                    }
                }
            }
        }

    }

    private fun observeRecipeSearch() {
        binding.postRecyclerView.isVisible = false
        lifecycleScope.launch {
            viewModel.searchRecipeState.collect {
                when (it) {
                    is Resource.Successful -> {
                        binding.recipeRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                        recipeAdapter.submitList(it.data?.toRecipeList())
                        Log.d("dia_recipe_search", "observeRecipeSearch:Success-> ${it.data}")
                    }
                    is Resource.Failure -> {
                        binding.recipeRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = it.msg
                        binding.progressBar.isVisible = false
                        Log.d("dia_recipe_search", "observeRecipeSearch:Failure")
                    }
                    is Resource.Loading -> {
                        binding.recipeRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true
                        Log.d("dia_recipe_search", "observeRecipeSearch:Loading")

                    }
                }
            }
        }

    }

    private fun observeCategory(){
        lifecycleScope.launch {
            viewModel.searchFlowCategory.collect {
                if (it.equals("post",true)){
                    toggleSearchView(it)
                    observePostSearch()
                    postAdapter.adapterClick {post->
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToForumPostDetailFragment(post)
                        findNavController().navigate(action)
                    }
                    return@collect
                }
                if (it.equals("article",true)){
                    toggleSearchView(it)
                    observeArticleSearch()
                    articleAdapter.adapterClick {article->
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToArticleFragment(article.url.toString())
                        findNavController().navigate(action)
                    }
                    return@collect
                }
                if (it.equals("recipe",true)){
                    toggleSearchView(it)
                    observeRecipeSearch()
                    recipeAdapter.adapterClick {recipe->
                        val action =
                            SearchFragmentDirections.actionSearchFragmentToRecipeDetailsFragment(recipe.id.toString())
                        findNavController().navigate(action)
                    }
                    return@collect
                }
/*
                when(it){
                    "Post"->{

                    }
                    "Article"->{

                    }
                    "Recipe"->{

                    }
                }
*/

            }
        }

    }
    private fun observeArticleSearch() {
        binding.postRecyclerView.isVisible = false
        lifecycleScope.launch {
            viewModel.searchArticleState.collect {
                when (it) {
                    is Resource.Successful -> {
                        binding.articleRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                        articleAdapter.submitList(it.data)
                        Log.d("dia_article_search", "observeArticleSearch:Success-> ${it.data}")
                    }
                    is Resource.Failure -> {
                        binding.articleRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = it.msg
                        binding.progressBar.isVisible = false
                        Log.d("dia_article_search", "observeArticleSearch:Failure")
                    }
                    is Resource.Loading -> {
                        binding.articleRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true
                        Log.d("dia_article_search", "observeArticleSearch:Loading")

                    }
                }
            }
        }
    }

    private fun toggleSearchView(category:String){
        binding.apply {
            articleRecyclerView.isVisible = category.equals("article",true)
            postRecyclerView.isVisible = category.equals("post",true)
            recipeRecyclerView.isVisible = category.equals("recipe",true)
            emptyStateTv.text = ""
        }
    }
}