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

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentProfileConsumatoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentProfileConsumatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposta un listener per il click sul pulsante che visualizza le recensioni del guidatore.
        binding.buttonVisualizzaRecensioni.setOnClickListener{
            // Viene sostituito il fragment corrente con il fragment RecensioniGuidatore che
            // mostrerà le recensioni lasciate dal consumatore
            requireActivity().supportFragmentManager.beginTransaction()
                // Viene aperto la schermata delle RecensioniGuidatoreFragment poiché questa classe rappresenta sia
                // le recensioni lasciate del consumatore sia quelle ricevute dal guidatore
                .replace(R.id.fragmentContainer, RecensioniGuidatoreFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}