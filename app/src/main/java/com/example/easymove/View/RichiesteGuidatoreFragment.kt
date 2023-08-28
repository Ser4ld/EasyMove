package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.databinding.FragmentRichiesteGuidatoreBinding

class RichiesteGuidatoreFragment : Fragment() {


    private var _binding: FragmentRichiesteGuidatoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRichiesteGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attesaTab = binding.tabLayout.newTab().setText("IN ATTESA")
        val acettateTab = binding.tabLayout.newTab().setText("ACCETTATE")
        val terminateTab = binding.tabLayout.newTab().setText("TERMINATE")

        binding.tabLayout.addTab(attesaTab)
        binding.tabLayout.addTab(acettateTab)
        binding.tabLayout.addTab(terminateTab)

    }
}