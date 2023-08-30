package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentVeicoliGuidatoreBinding
import com.example.easymove.model.Veicolo
import com.google.firebase.auth.FirebaseAuth

class VeicoliGuidatoreFragment : Fragment() {

    private var _binding: FragmentVeicoliGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var adapter: MyAdapterVeicoli
    private val list: ArrayList<Veicolo> = arrayListOf()
    private val IdTemporaneo = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVeicoliGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        binding.fabButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        adapter = MyAdapterVeicoli(veicoliViewModel,userViewModel,ArrayList(),"", null)
        binding.recyclerView.adapter = adapter


        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
            if (veicoliList.isEmpty()) {
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show()
            } else {
                if (IdTemporaneo != null) {
                    adapter.updateData(veicoliViewModel.filterVeicoliByUserId(IdTemporaneo, veicoliList))
                }
            }
        }

    }

}