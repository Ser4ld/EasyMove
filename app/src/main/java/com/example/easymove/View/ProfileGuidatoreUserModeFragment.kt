package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.R
import com.example.easymove.databinding.FragmentProfileConsumatoreBinding
import com.example.easymove.databinding.FragmentProfileGuidatoreUserModeBinding


class ProfileGuidatoreUserModeFragment : Fragment() {

    private var _binding: FragmentProfileGuidatoreUserModeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileGuidatoreUserModeBinding.inflate(inflater, container, false)
        return binding.root
    }

}