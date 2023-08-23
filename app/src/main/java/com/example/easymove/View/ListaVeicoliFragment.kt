package com.example.easymove.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentListaVeicoliBinding
import com.example.easymove.model.Veicoli
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaVeicoliFragment : Fragment() {

    private var _binding:FragmentListaVeicoliBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: ArrayList<Veicoli>
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaVeicoliBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityOrigin = arguments?.getString("cityOrigin")

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.fabButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
        list = arrayListOf()

        db = FirebaseFirestore.getInstance()
        db.collection("vans").get().addOnSuccessListener { snapshot ->

            if (!snapshot.isEmpty) {
                for (data in snapshot.documents) {
                    val van: Veicoli? = data.toObject(Veicoli::class.java)
                    if (van != null
                    // && van.citta == cityOrigin
                    ) {
                        list.add(van)
                    }
                    else Toast.makeText(requireContext(), "Non ci sono annunci", Toast.LENGTH_SHORT).show()
                }
            }

            binding.recyclerView.adapter = MyAdapterVeicoli(list)
        }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT).show()
            }

    }



}