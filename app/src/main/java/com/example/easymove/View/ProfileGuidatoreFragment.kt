package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentProfileGuidatoreBinding


class ProfileGuidatoreFragment : Fragment() {
    private var _binding: FragmentProfileGuidatoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recensioneViewModel= ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)


        userViewModel.userDataLiveData.observe(viewLifecycleOwner){userData ->
            if(userData!= null)
            {
                recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner){ recensioniList->
                    if (recensioniList!=null) {
                        var media = recensioneViewModel.mediaRecensioniFiltrate(userData.id, recensioniList)
                        binding.ratingBarRecensione.rating = media
                    }
                }
                veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
                    if (veicoliList.isNotEmpty()) {
                       binding.textVeicoliGuidatore2.text = " ${veicoliViewModel.countVeicoliByUserId(userData.id, veicoliList).toString()}"
                    } else binding.textVeicoliGuidatore2.text=" 0"
                }
            }
        }




        binding.buttonVisualizzaVeicoli.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, VeicoliGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.buttonRecensioni.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecensioniGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}