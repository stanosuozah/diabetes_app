package com.jhealth.diabetesapp.presentation.onboarding_auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!
    private val fUser = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fUser!= null){
            val action =
                com.jhealth.diabetesapp.presentation.onboarding_auth.WelcomeFragmentDirections.actionWelcomeFragmentToHomeFragment()
            findNavController().navigate(action)
            return
        }
        binding.loginBtn.setOnClickListener {
            val action =
                WelcomeFragmentDirections.actionWelcomeFragmentToAuthFragment(
                    "l"
                )
            findNavController().navigate(action)
        }
        binding.signUpBtn.setOnClickListener {
            val action =
                WelcomeFragmentDirections.actionWelcomeFragmentToAuthFragment(
                    "s"
                )
            findNavController().navigate(action)
        }
    }


}