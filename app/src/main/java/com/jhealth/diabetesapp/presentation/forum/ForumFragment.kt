package com.jhealth.diabetesapp.presentation.forum

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhealth.diabetesapp.R
import com.jhealth.diabetesapp.databinding.FragmentForumBinding
import com.jhealth.diabetesapp.domain.model.Posts
import com.jhealth.diabetesapp.presentation.adapter.PostsAdapter
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

class ForumFragment : Fragment() {
    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val postsAdapter by lazy { PostsAdapter() }
    private val viewModel by activityViewModels<DiabetesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postsRecyclerView.adapter = postsAdapter
        binding.searchPlate.setOnClickListener {
            findNavController().navigate(R.id.action_global_searchFragment)
        }
        binding.floatingActionButton.setOnClickListener {
            val action =
                ForumFragmentDirections.actionForumFragmentToAddPostFragment()
            findNavController().navigate(action)
        }
        postsAdapter.adapterClick {
            val action =
                ForumFragmentDirections.actionForumFragmentToForumPostDetailFragment(
                    it
                )
            findNavController().navigate(action)
        }
        setUpSpinner()
        viewModel.addAllPostSnapshotListener()
        observeAllPosts()
    }

    private fun observeAllPosts(){
        lifecycleScope.launch {
            viewModel.allPostState.collect {state->
                binding.retryPostsText.isVisible = state is Resource.Failure
                when (state) {
                    is Resource.Successful -> {
                        binding.postsRecyclerView.isVisible = true
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = false
                        state.data?.let { it -> updateListPerOrder(it) }
                    }
                    is Resource.Failure -> {
                        binding.postsRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = true
                        binding.emptyStateTv.text = state.msg
                        binding.progressBar.isVisible = false
                    }
                    is Resource.Loading -> {
                        binding.postsRecyclerView.isVisible = false
                        binding.emptyStateTv.isVisible = false
                        binding.progressBar.isVisible = true

                    }
                }
            }
        }

    }

    private fun setUpSpinner() {
        binding.orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                binding.orderSpinner.setSelection(pos)
                viewModel.togglePostSortOrder(binding.orderSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun updateListPerOrder(list: List<Posts>){
        lifecycleScope.launch {
            viewModel.postSortOrder.collect{
                Log.d("dia_sort_order", "updateListPerOrder: order -> $it")
                if (it.equals("Ascending",true)){
                    postsAdapter.submitList(list.reversed())
                    return@collect
                }
                postsAdapter.submitList(list)
            }
        }
    }



}