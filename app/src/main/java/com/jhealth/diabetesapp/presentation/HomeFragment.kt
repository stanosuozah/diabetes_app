package com.jhealth.diabetesapp.presentation

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentHomeBinding
import com.jhealth.diabetesapp.presentation.adapter.CategoryItemAdapter
import com.jhealth.diabetesapp.presentation.adapter.HomePostsItemAdapter
import com.jhealth.diabetesapp.presentation.adapter.NewsItemAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val articleAdapter by lazy { NewsItemAdapter() }
    private val recipesAdapter by lazy { CategoryItemAdapter() }
    private val postsAdapter by lazy { HomePostsItemAdapter() }
    private val fUser = Firebase.auth.currentUser
    private  val viewModel by activityViewModels<DiabetesViewModel>()
    private var exitAppToastStillShowing = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.addAllPostSnapshotListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = true
            exitApp()
        }

        binding.apply {
           fUser?.email?.let {
                usernameText.text =it
               viewModel.getCurrentUser(it)
            }
            articleRecyclerView.adapter = articleAdapter
            recipeRecyclerView.adapter = recipesAdapter
            postsRecyclerView.adapter = postsAdapter
            profileImg.setOnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToAccountProfileFragment()
                findNavController().navigate(action)
            }
            searchPlate.setOnClickListener {
                findNavController().navigate(R.id.action_global_searchFragment)
            }
        }
        recipesAdapter.adapterClick {
            val action =
                HomeFragmentDirections.actionHomeFragmentToRecipeDetailsFragment(
                    it.id.toString()
                )
            findNavController().navigate(action)
        }
        postsAdapter.adapterClick {
            val action =
                HomeFragmentDirections.actionHomeFragmentToForumPostDetailFragment(
                    it
                )
            findNavController().navigate(action)
        }
        articleAdapter.adapterClick {
            val action =
                HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                    it.url!!
                )
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewModel.firstLaunch.collect{
                if (it){
                    viewModel.loadAllMeal()
                    viewModel.loadTrendingNews()
                    viewModel.toggleFirstLaunch( false)
                }
            }
        }
        observeAllPosts()
        observeAllMealStateFlow()
        observeAllNewsStateFlow()

    }

    private fun observeAllMealStateFlow() {
        lifecycleScope.launch {
            viewModel.allMealsStateFlow.collect { state ->
                binding.retryRecipesText.isVisible = state is Resource.Failure
                when (state) {
                    is Resource.Successful -> {
                        state.data?.let {
                            recipesAdapter.submitList(it.toRecipeList())
                        } ?: Toast.makeText(
                            requireContext(),
                            "Some nullable error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.recipeRecyclerView.isVisible = true
                        binding.recipeEmptyStateTv.isVisible = false
                        binding.recipeProgressBar.isVisible = false
                    }
                    is Resource.Failure -> {
                        binding.recipeRecyclerView.isVisible = false
                        binding.recipeEmptyStateTv.isVisible = true
                        binding.recipeEmptyStateTv.text = state.msg
                        binding.recipeProgressBar.isVisible = false
                        binding.retryRecipesText.setOnClickListener {
                            viewModel.loadAllMeal()
                        }
                    }
                    is Resource.Loading -> {
                        binding.recipeRecyclerView.isVisible = false
                        binding.recipeEmptyStateTv.isVisible = false
                        binding.recipeProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    private fun observeAllPosts(){
        lifecycleScope.launch {
            viewModel.allPostState.collect {state->
                binding.retryPostsText.isVisible = state is Resource.Failure
                when (state) {
                    is Resource.Successful -> {
                        binding.postsRecyclerView.isVisible = true
                        binding.postsEmptyStateTv.isVisible = false
                        binding.postsProgressBar.isVisible = false
                        postsAdapter.submitList(state.data)
                    }
                    is Resource.Failure -> {
                        binding.postsRecyclerView.isVisible = false
                        binding.postsEmptyStateTv.isVisible = true
                        binding.postsEmptyStateTv.text = state.msg
                        binding.postsProgressBar.isVisible = false
                        binding.retryPostsText.setOnClickListener {
                            viewModel.loadAllPost()
                        }
                    }
                    is Resource.Loading -> {
                        binding.postsRecyclerView.isVisible = false
                        binding.postsEmptyStateTv.isVisible = false
                        binding.postsProgressBar.isVisible = true

                    }
                }
            }
        }

    }

    private fun observeAllNewsStateFlow() {
        lifecycleScope.launch {
            viewModel.allArticlesStateFlow.collect { state ->
                binding.retryArticleText.isVisible = state is Resource.Failure
                when (state) {
                    is Resource.Successful -> {
                        state.data?.let {
                            articleAdapter.submitList(it.articles)
                        } ?: Toast.makeText(
                            requireContext(),
                            "Some nullable error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.articleRecyclerView.isVisible = true
                        binding.articleEmptyStateTv.isVisible = false
                        binding.articleProgressBar.isVisible = false
                    }
                    is Resource.Failure -> {
                        binding.articleRecyclerView.isVisible = false
                        binding.articleEmptyStateTv.isVisible = true
                        binding.articleEmptyStateTv.text = state.msg
                        binding.articleProgressBar.isVisible = false
                        binding.retryArticleText.setOnClickListener {
                            viewModel.loadTrendingNews()
                        }
                    }
                    is Resource.Loading -> {
                        binding.articleRecyclerView.isVisible = false
                        binding.articleEmptyStateTv.isVisible = false
                        binding.articleProgressBar.isVisible = true
                    }
                }
            }
        }
    }


    private val exitAppTimer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            exitAppToastStillShowing = false
        }
    }
    private fun exitApp() {
        if (exitAppToastStillShowing) {
            requireActivity().finish()
            return
        }

        Toast.makeText(this.requireContext(), "Tap again to exit", Toast.LENGTH_SHORT)
            .show()
        exitAppToastStillShowing = true
        exitAppTimer.start()
    }




}