package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.easymove.R
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.FragmentIndexBinding
import com.example.easymove.databinding.FragmentProfileBinding
import com.example.easymove.databinding.IndexBinding
import com.example.easymove.repository.UserRepository

class IndexFragment : Fragment() {

    private var _binding: FragmentIndexBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIndexBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButtonIndex.setOnClickListener {
            requireFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.signupButtonIndex.setOnClickListener{
            requireFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}