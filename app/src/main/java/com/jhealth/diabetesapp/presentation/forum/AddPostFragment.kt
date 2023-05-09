package com.jhealth.diabetesapp.presentation.forum

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.databinding.FragmentAddPostBinding
import com.jhealth.diabetesapp.domain.model.Posts
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.jhealth.diabetesapp.util.RequestState
import kotlinx.coroutines.launch
import java.util.*


class AddPostFragment : Fragment() {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiabetesViewModel>()
    private val fUser = Firebase.auth.currentUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.postText.setOnClickListener {
            addPost()

        }
    }

    private fun addPost() {
        val title = binding.postEdt.text.toString().trim()
        val content = binding.postContentEdt.text.toString().trim()


        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Post title can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.length > 250) {
            Toast.makeText(requireContext(), "Post length shouldn't exceed 250", Toast.LENGTH_SHORT)
                .show()
            return
        }
        fUser?.email?.let {
            val post = Posts(UUID.randomUUID().toString(), it, content, title)
            viewModel.sendPost(post)
        } ?: Toast.makeText(requireContext(), "Email is not provided", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            viewModel.sendPostState.collect {
                binding.progressBar.isVisible = it is RequestState.Loading
                when (it) {
                    is RequestState.Successful -> {
                        findNavController().navigateUp()
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Error while posting: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

    }
}