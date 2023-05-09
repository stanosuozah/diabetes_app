package com.jhealth.diabetesapp.presentation.recipe

import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.data.dto.FullMealDTO
import com.jhealth.diabetesapp.databinding.FragmentRecipeDetailsBinding
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch


class RecipeDetailsFragment : Fragment() {
    private var _binding: FragmentRecipeDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<RecipeDetailsFragmentArgs>()
    private val viewModel by activityViewModels<DiabetesViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mealId = args.args
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        observeMealState(mealId)
    }


    private fun observeMealState(id:String) {
        viewModel.getMealById(id)
        lifecycleScope.launch {
            viewModel.currentMealState.collect { state ->
                when (state) {
                    is Resource.Successful -> {
                      state.data?.let {data->
                         showInfo( data.meals[0])
                          binding.playImg.setOnClickListener {
                             val action =
                                 RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToRecipeDemoFragment(
                                     data.meals[0].strYoutube
                                 )
                              findNavController().navigate(action)
                         }
                      } ?: Toast.makeText(
                          requireContext(),
                          "Some nullable error occurred",
                          Toast.LENGTH_SHORT
                      ).show()
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Failure -> {
                        binding.mealInfoHolder.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = state.msg
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Loading -> {
                        binding.mealInfoHolder.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true

                    }
                }

            }
        }

    }

    private fun showInfo(meal: FullMealDTO) {
        binding.apply {
            mealInfoHolder.isVisible = true
            titleText.text = meal.toRecipe().recipe_title
            tagsText.text = meal.toRecipe().recipe_category
            factContentText.text = meal.toRecipe().nutrition_fact
            ingredientsContentText.text =meal.toRecipe().ingredient
            lifecycleScope.launch {
                viewModel.showEllipse.collect{show->
                    readMoreText.setOnClickListener{
                        viewModel.toggleShowEllipse(!show)
                    }
                    if (show){
                        readMoreText.text = "Read more..."
                        val maxLength =150
                        val fArray: Array<InputFilter?> = arrayOfNulls<InputFilter>(1)
                        fArray[0] = InputFilter.LengthFilter(maxLength)
                        instructionsContentText.filters = fArray
                        instructionsContentText.ellipsize = TextUtils.TruncateAt.END
                        instructionsContentText.text = meal.instructions
                    }else{
                        readMoreText.text = "Read less..."
                        val maxLength = meal.instructions.length
                        val fArray: Array<InputFilter?> = arrayOfNulls<InputFilter>(1)
                        fArray[0] = InputFilter.LengthFilter(maxLength)
                        instructionsContentText.filters = fArray
                        instructionsContentText.ellipsize = null
                        instructionsContentText.text = meal.instructions
                    }
                }
            }

            backgroundImg.load(meal.mealImg) {
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
            }
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.toggleShowEllipse(false)
    }
}