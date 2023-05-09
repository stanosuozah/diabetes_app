package com.jhealth.diabetesapp.presentation.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jhealth.diabetesapp.databinding.FragmentAccountProfileBinding
import com.jhealth.diabetesapp.presentation.arch.DiabetesViewModel

class AccountProfileFragment : Fragment() {
    private var _binding: FragmentAccountProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<DiabetesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
            Firebase.auth.currentUser?.email?.let {
                authorText.text = it
            } ?: Toast.makeText(requireContext(),"No user at the moment ",Toast.LENGTH_SHORT).show()
            editProfileHolder.setOnClickListener {
                val action =
                    AccountProfileFragmentDirections.actionAccountProfileFragmentToEditProfileFragment()
                findNavController().navigate(action)
            }
            signOutBtn.setOnClickListener {
                Firebase.auth.signOut()
                viewModel.updateCurrentUser()
                viewModel.resetSomeNecessaryStates()
                val action =
                    AccountProfileFragmentDirections.actionAccountProfileFragmentToWelcomeFragment()
                findNavController().navigate(action)
            }
        }
    }


}