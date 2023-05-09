package com.jhealth.diabetesapp.presentation.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.jhealth.diabetesapp.databinding.FragmentEditProfileBinding
import com.jhealth.diabetesapp.domain.model.User
import com.jhealth.diabetesapp.util.RequestState
import com.tutorial.messageme.data.utils.Resource
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiabetesViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        lifecycleScope.launch {
            viewModel.currentUser.collect { state ->
                when (state) {
                    is Resource.Successful -> {
                        state.data?.let {data->
                            binding.apply {
                                fNameEdt.setText(data.firstName)
                                lNameEdt.setText(data.lastName)
                                emailEdt.setText(data.email)
                                updateBtn.setOnClickListener {
                                    updateUserInfo(data)
                                    observeUpdateState(data.email)
                                }
                            }
                        } ?: Toast.makeText(
                            requireContext(),
                            "Some nullable error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            state.msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            "Getting User Info...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun updateUserInfo(user: User){
        val fName = binding.fNameEdt.text.toString().trim()
        val lName = binding.lNameEdt.text.toString().trim()
        if (fName.isEmpty() || lName.isEmpty() ){
            Toast.makeText(
                requireContext(),
                "First and Last Name can't be empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
       val updatedInfo = user.copy(firstName = fName, lastName = lName)
        viewModel.updateCurrentUserInfo(updatedInfo)
    }

    private fun observeUpdateState(email:String){
        lifecycleScope.launch {
            viewModel.currentUserUpdateState.collect {
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(
                            requireContext(),
                            "Update Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.toggleUpdateProfileState(RequestState.NonExistent)
                        viewModel.getCurrentUser(email)
                        Log.d("dia_update", "update state-> ${it.data}")
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Update Failed: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("dia_update", "update state error-> ${it.msg}")
                    }
                    is RequestState.Loading->{
                        Toast.makeText(
                            requireContext(),
                            "Updating...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> Unit
                }
            }
        }

    }


}