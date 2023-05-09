package com.jhealth.diabetesapp.presentation.article

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentArticleHomeBinding
import com.jhealth.diabetesapp.presentation.adapter.DiabetesNewsAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class ArticleHomeFragment : Fragment() {
    private var _binding: FragmentArticleHomeBinding? = null
    private val binding get() = _binding!!
    private val articlesAdapter by lazy { DiabetesNewsAdapter() }
    private val viewModel by activityViewModels<DiabetesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArticleHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.articlesRecyclerView.adapter = articlesAdapter
        binding.searchPlate.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchFragment)
        }
       // articlesAdapter.submitList(DummyObject.getData())
        articlesAdapter.adapterClick {
            val action =
                ArticleHomeFragmentDirections.actionArticleHomeFragmentToArticleFragment(it.url!!)
            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            viewModel.firstLaunch.collect{
                if (it){
                    viewModel.loadTrendingNews()
                    viewModel.toggleFirstLaunch( false)
                }
            }
        }

        observeTrendingNews()
    }

    private fun observeTrendingNews(){
        lifecycleScope.launch {
            viewModel.allArticlesStateFlow.collect{ state->
                binding.retryArticleText.isVisible = state is Resource.Failure
                when(state){
                    is Resource.Successful->{
                        state.data?.let {
                            articlesAdapter.submitList(it.articles.shuffled())
                        } ?: Toast.makeText(
                            requireContext(),
                            "Some nullable error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.articlesRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Loading->{
                        binding.articlesRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                    is Resource.Failure->{
                        binding.articlesRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.progressBar.isVisible = false
                        binding.emptyStateTv.text = state.msg
                        binding.retryArticleText.setOnClickListener {
                            viewModel.loadTrendingNews()
                        }
                    }
                }

            }
        }
    }


}