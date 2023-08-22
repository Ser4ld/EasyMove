package com.example.easymove.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.FragmentResetPasswordBinding
import com.example.easymove.repository.UserRepository
import com.example.easymove.viewmodel.ResetPasswordViewModel


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var ResetPasswordViewModel: ResetPasswordViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ResetPasswordViewModel = ResetPasswordViewModel(UserRepository())

        binding.floatingActionButton.setOnClickListener{
            requireFragmentManager().popBackStack()
        }

        binding.resetbtn.setOnClickListener(){
        }

    }



    }