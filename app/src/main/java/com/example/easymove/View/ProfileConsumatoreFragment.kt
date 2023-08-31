package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.R
import com.example.easymove.databinding.FragmentProfileConsumatoreBinding
import com.example.easymove.databinding.FragmentProfileGuidatoreBinding

class ProfileConsumatoreFragment : Fragment() {

    private var _binding: FragmentProfileConsumatoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileConsumatoreBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonVisualizzaRecensioni.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecensioniGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}