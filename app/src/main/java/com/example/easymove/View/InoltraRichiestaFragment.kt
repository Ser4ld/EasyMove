package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.R
import com.example.easymove.databinding.FragmentInoltraRichiestaBinding

class InoltraRichiestaFragment : Fragment() {

    private var _binding: FragmentInoltraRichiestaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInoltraRichiestaBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val arguments = arguments
        if (arguments != null) {
            val modello = arguments.getString("modello")
            val targa = arguments.getString("targa")
            val capienza = arguments.getString("capienza")

            // Now you can use these values as needed
            binding.textViewVeicolo2.text = modello
            // Similarly, set other values to appropriate views
        }
    }


}