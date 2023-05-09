package com.jhealth.diabetesapp.presentation.onboarding_auth

import android.os.Bundle
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jhealth.diabetesapp.databinding.FragmentAuthBinding
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel
import com.jhealth.diabetesapp.util.RequestState
import kotlinx.coroutines.launch


class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<AuthFragmentArgs>()
    private val viewModel by activityViewModels<DiabetesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.args == "l") {
            viewModel.toggleAuthState(true)
        } else {
            viewModel.toggleAuthState(false)
        }

        lifecycleScope.launch {
            viewModel.authState.collect {
                if (it) {
                    binding.loginLayouts.root.isVisible = true
                    binding.signUpLayouts.root.isVisible = false
                    bindLogin()
                } else {
                    binding.loginLayouts.root.isVisible = false
                    binding.signUpLayouts.root.isVisible = true
                    bindSignUp()
                }

            }
        }

    }

    private fun bindSignUp(){
        binding.signUpLayouts.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }

            createAccBtn.setOnClickListener {

                val email = emailEdt.text.toString()
                val password = passwordEdt.text.toString()
                val fName = fNameEdt.text.toString()
                val lName = lNameEdt.text.toString()

                if (email.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (password.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (!email.trim().contains("@gmail.com")) {
                    Toast.makeText(
                        requireContext(),
                        "Please input a valid email address '@gmail.com'",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (!binding.signUpLayouts.acceptCheckbox.isChecked) {
                    Toast.makeText(requireContext(), "Accept the T&C", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                showDialog(false) {
                    viewModel.signUp(fName, lName, email, password)
                }
            }
        }
        observeSignUpState()
    }

    private fun bindLogin() {
        binding.loginLayouts.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
            createText.setOnClickListener {
                viewModel.toggleAuthState(false)
            }


            loginBtn.setOnClickListener {
                val email = emailEdt.text.toString()
                val password = passwordEdt.text.toString()

                if (email.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Email field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                if (password.trim().isEmpty()) {
                    Toast.makeText(requireContext(), "Password field is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                viewModel.login(email, password)
                observeLoginState()
            }

        }
    }


    private fun showDialog(state: Boolean, action: () -> Unit) {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        if (state) {
            dialog.setTitle("Verification Notice")
            dialog.setMessage("A verification link has been sent to your email, please verify your email to complete your profile.")
            dialog.setCancelable(false)
        } else {
            dialog.setTitle("Information Notice")
            dialog.setMessage("Please confirm your email is valid and active before proceeding as a verification link will be sent to it.")
            dialog.setCancelable(true)
        }
        dialog.setNegativeButton("Dismiss") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.setPositiveButton("Proceed") { dialogInterface, _ ->
            action()
            dialog.setCancelable(true)
            dialogInterface.dismiss()
        }
        dialog.show()

    }

    private fun observeLoginState(){
        lifecycleScope.launch {
            viewModel.loginState.collect {
                binding.loginLayouts.progressBar.isVisible = it is RequestState.Loading
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(
                            requireContext(),
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("dia_auth", "login state-> ${it.data}")
                        viewModel.toggleLoginState(RequestState.NonExistent)
                        val action =
                            AuthFragmentDirections.actionAuthFragmentToHomeFragment()
                        findNavController().navigate(action)
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(
                            requireContext(),
                            "Login Failed: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.toggleLoginState(RequestState.NonExistent)
                        Log.d("dia_auth", "login state error-> ${it.msg}")
                    }
                    else -> Unit
                }
            }
        }

    }
    private fun observeSignUpState(){
        lifecycleScope.launch {
            viewModel.signUpState.collect {
                binding.signUpLayouts.progressBar.isVisible = it is RequestState.Loading
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(
                            requireContext(),
                            "Sign up Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("dia_auth", "bindSignUp: signup state-> ${it.data}")
                        showDialog(true) {
                            viewModel.toggleSignUpState(RequestState.NonExistent)
                            val action =
                                AuthFragmentDirections.actionAuthFragmentToHomeFragment()
                            findNavController().navigate(action)
                            viewModel.sendEmailVerification()
                        }
                    }
                    is RequestState.Failure -> {
                        viewModel.toggleSignUpState(RequestState.NonExistent)
                        Toast.makeText(
                            requireContext(),
                            "Sign up Failed: ${it.msg}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("dia_auth", "bindSignUp: signup state error-> ${it.msg}")
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun observeEmailLink(){
        lifecycleScope.launch {
            viewModel.emailVerificationState.collect {
                when (it) {
                    is RequestState.Successful -> {
                        Toast.makeText(requireContext(),"Email Sent Successfully",Toast.LENGTH_SHORT).show()
                    }
                    is RequestState.Failure -> {
                        Toast.makeText(requireContext(),"Some error occurred: ${it.msg}",Toast.LENGTH_SHORT).show()
                    }
                    is RequestState.Loading -> {
                        Toast.makeText(requireContext(),"Sending Email...",Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
    }

}