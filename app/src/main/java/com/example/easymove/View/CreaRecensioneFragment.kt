package com.example.easymove.View

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentCreaRecensioneBinding


class CreaRecensioneFragment : Fragment() {

    private var _binding: FragmentCreaRecensioneBinding? = null
    private val binding get() = _binding!!

    private lateinit var  recensioneViewModel : RecensioneViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreaRecensioneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recensioneViewModel =
            ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)

        binding.btnInviaRecensione.setOnClickListener {

            recensioneViewModel.creaRecensione(
                binding.ratingBar.rating.toString(),
                binding.etDescrizione.text.toString()
            )
            { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }


       binding.etDescrizione.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                binding.characterCountTextView.text = "$currentLength/300"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
}