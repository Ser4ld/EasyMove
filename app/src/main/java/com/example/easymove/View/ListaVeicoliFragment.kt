package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentListaVeicoliBinding
import com.example.easymove.model.Veicolo
import com.google.firebase.firestore.FirebaseFirestore

class ListaVeicoliFragment : Fragment() {

    private var _binding: FragmentListaVeicoliBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListaVeicoliBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)

        binding.fabButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        /*db = FirebaseFirestore.getInstance()

        db.collection("vans").get().addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                val van = document.toObject(Veicolo::class.java)
                if (van != null) {
                    list.add(van)
                }
            }
            if (list.isEmpty()) {
                Toast.makeText(requireContext(), "Non ci sono annunci", Toast.LENGTH_SHORT).show()
            }
            binding.recyclerView.adapter = MyAdapterVeicoli(list)
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT).show()
        }*/

        veicoliViewModel.startVeicoliListener()

        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->

            val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
            val currentPosition = layoutManager.findFirstVisibleItemPosition()
            Toast.makeText(requireContext(), "Posizione corrente: $currentPosition", Toast.LENGTH_SHORT).show()

            if (veicoliList.isEmpty()) {
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.recyclerView.adapter = MyAdapterVeicoli(ArrayList(veicoliList))

                if (currentPosition != RecyclerView.NO_POSITION) {
                    layoutManager.scrollToPosition(currentPosition)
                }

            }
            }
        }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}