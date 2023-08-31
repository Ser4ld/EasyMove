package com.example.easymove.View

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
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
    private lateinit var richiestaViewModel: RichiestaViewModel

    private lateinit var adapter: MyAdapterVeicoli
    private lateinit var utenteId:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVeicoliGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        veicoliViewModel.startVeicoliListener()

        binding.fabButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        userViewModel.userDataLiveData.observe(viewLifecycleOwner){currentUser->
            if (currentUser != null) {
                utenteId = currentUser.id
            }
        }


        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner){richiesteList ->
            if(richiesteList != null)
            adapter = MyAdapterVeicoli(veicoliViewModel,userViewModel,richiestaViewModel,ArrayList(),richiesteList,"", null)
            binding.recyclerView.adapter = adapter

        }


        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->

            var veicoliFiltrati= veicoliViewModel.filterVeicoliByUserId(utenteId, veicoliList)
            if(veicoliFiltrati.isEmpty()){
                binding.emptyLayout.visibility = View.VISIBLE
            } else {
                binding.emptyLayout.visibility = View.GONE
            }
            adapter.updateData(veicoliFiltrati)

        }
    }
}