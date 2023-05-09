package com.jhealth.diabetesapp.presentation.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentRecipesBinding
import com.jhealth.diabetesapp.presentation.adapter.CategoriesAdapter
import com.jhealth.diabetesapp.presentation.adapter.CategoryItemAdapter
import com.jhealth.diabetesapp.presentation.adapter.FullRecipesAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private val trendingRecipesAdapter by lazy { CategoryItemAdapter() }
    private val categoriesAdapter by lazy { CategoriesAdapter() }
    private val newRecipesAdapter by lazy { FullRecipesAdapter() }
    private val viewModel by activityViewModels<DiabetesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.categoriesRecyclerView.adapter = categoriesAdapter
        binding.trendingRecipeRecyclerView.adapter = trendingRecipesAdapter
        binding.newRecipesRecyclerView.adapter = newRecipesAdapter

        binding.searchPlate.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchFragment)
        }

       lifecycleScope.launch {
           viewModel.firstLaunch.collect{
               if (it){
                   viewModel.loadAllMeal()
                   viewModel.toggleFirstLaunch( false)
               }
           }
       }
        observeCategory()
        observeAllMealStateFlow()
        categoriesAdapter.adapterClickListener {
            viewModel.setCategoryQuery(it.title)
            viewModel.onCategorySelected(it)
        }
        trendingRecipesAdapter.adapterClick {
            val action =
                RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailsFragment(
                    it.id.toString()
                )
            findNavController().navigate(action)
        }
        newRecipesAdapter.adapterClick {
            val action =
                RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailsFragment(
                    it.id.toString()
                )
            findNavController().navigate(action)
        }
    }

    private fun observeAllMealStateFlow() {
        lifecycleScope.launch {
            viewModel.allMealsStateFlow.collect { state ->
                binding.retryRecipesText.isVisible = state is Resource.Failure
                when (state) {
                    is Resource.Successful -> {
                        state.data?.let {
                            newRecipesAdapter.submitList(it.toRecipeList())
                            trendingRecipesAdapter.submitList(it.toRecipeList())
                        } ?: Toast.makeText(
                            requireContext(),
                            "Some nullable error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.newRecipesRecyclerView.isVisible = true
                        binding.trendingRecipeRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Failure -> {
                        binding.newRecipesRecyclerView.isVisible = false
                        binding.trendingRecipeRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = state.msg
                        binding.progressBar.isVisible = false
                        binding.retryRecipesText.setOnClickListener {
                            viewModel.loadAllMeal()
                        }
                    }
                    is Resource.Loading -> {
                        binding.newRecipesRecyclerView.isVisible = false
                        binding.trendingRecipeRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true

                    }
                }
            }
        }
    }

    private fun observeCategory() {
        lifecycleScope.launch {
            viewModel.selectedCategoriesFlow.collect {
                categoriesAdapter.submitList(it)
            }
        }
    }

}