package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRecensioni
import com.example.easymove.databinding.FragmentRecensioniGuidatoreBinding

class RecensioniGuidatoreFragment : Fragment() {

    private var _binding: FragmentRecensioniGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var adapter: MyAdapterRecensioni

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecensioniGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        adapter = MyAdapterRecensioni(ArrayList())
        binding.recyclerView.adapter = adapter


        binding.fabButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recensioneViewModel.startVeicoliListener()
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioniList ->
            if (recensioniList.isEmpty()) {
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateRecensioni(recensioniList)
            }
        }
    }


}