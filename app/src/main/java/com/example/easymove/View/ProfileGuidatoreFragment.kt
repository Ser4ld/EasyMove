package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentProfileGuidatoreBinding
import java.util.Calendar


class ProfileGuidatoreFragment : Fragment() {

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentProfileGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentProfileGuidatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione dei ViewModel necessari
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recensioneViewModel= ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)


        // Osservatore per i dati dell'utente per ottenere i dati dell'utente
        userViewModel.userDataLiveData.observe(viewLifecycleOwner){userData ->
            if(userData!= null)
            {
                // Osservatore per le recensioni dell'utente per ottenere la lista di recensioni filtrate dell'utente corrente
                recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner){ recensioniList->
                    if (recensioniList!=null) {
                        // Calcola e imposta la media delle recensioni filtrate per utente
                        var media = recensioneViewModel.mediaRecensioniFiltrate(userData.id, recensioniList, userData.userType)
                        binding.ratingBarRecensione.rating = media
                    }
                }
                // Osservatore per i veicoli dell'utente
                veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
                    if (veicoliList.isNotEmpty()) {

                        // Imposta il numero di veicoli dell'utente nel testo della vista
                        binding.textVeicoliGuidatore2.text = " ${veicoliViewModel.countVeicoliByUserId(userData.id, veicoliList)}"
                    }
                    // Se l'utente non ha veicoli, imposta il testo a "0"
                    else binding.textVeicoliGuidatore2.text=" 0"
                }
            }
        }


        // Listener che al click del bottone buttonVisualizzaVeicoli permette
        // di passare alla schermata di visualizzazione dei veicoli del guidatore
        binding.buttonVisualizzaVeicoli.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, VeicoliGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }

        // Listener che al click del bottone buttonRecensioni permette
        // di passare alla schermata di visualizzazione delle recensioni lasciate al guidatore
        binding.buttonRecensioni.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecensioniGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}