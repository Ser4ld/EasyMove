package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.R
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentListaVeicoliBinding
import com.google.firebase.auth.FirebaseAuth

class ListaVeicoliFragment : Fragment() {

    private var _binding: FragmentListaVeicoliBinding? = null
    private val binding get() = _binding!!

    private lateinit var cityOrigin: String
    private lateinit var postCodeOrigin: String

    private lateinit var origin: String
    private lateinit var destination: String



    private lateinit var veicoliViewModel: VeicoliViewModel

    private lateinit var adapter: MyAdapterVeicoli

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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        adapter = MyAdapterVeicoli(veicoliViewModel,ArrayList())
        binding.recyclerView.adapter = adapter

        val argument=arguments
        if (argument != null){
            cityOrigin = argument.getString("originCity").toString()
            postCodeOrigin = argument.getString("originPostCode").toString()
            origin = argument.getString("origin").toString()
            destination = argument.getString("destination").toString()

            Log.d("Origin Data", "City: $cityOrigin, Postal Code: $postCodeOrigin")
        }

        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
            if (veicoliList.isEmpty()) {
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show()
            } else {
                val sortedVeicoliList = veicoliList.sortedBy { it.modello }
                veicoliViewModel.richiestaClickedEvent.observe(viewLifecycleOwner) { position ->
                    if (position != -1) {

                        val selectedVehicle =
                            sortedVeicoliList[position] // Usa la posizione per ottenere il veicolo dalla lista

                        val bundle = Bundle()
                        bundle.putString("modello", selectedVehicle.modello)
                        bundle.putString("targa", selectedVehicle.targa)
                        bundle.putString("capienza", selectedVehicle.capienza)
                        bundle.putString("id_guidatore", selectedVehicle.id)
                        bundle.putString("destination", destination)
                        bundle.putString("origin", origin)

                        //passaggio informazioni origine e destinazione
                        /*val argument = arguments
                        if (argument != null) {
                            val destinazione = argument.getString("destinazione")
                            bundle.putString("destinazione", destinazione)
                        }*/
                        //reset valore liveData altrimenti rimane attivo l'evento di click


                        val inoltraRichiestaFragment = InoltraRichiestaFragment()
                        inoltraRichiestaFragment.arguments = bundle

                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, inoltraRichiestaFragment)
                            .addToBackStack(null)
                            .commit()
                        // Esegui la transazione del fragment come desiderato
                        veicoliViewModel.resetRichiestaClickedEvent()

                    }
                }
            }
        }



        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
            if (veicoliList.isEmpty()) {
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateData(veicoliViewModel.filterVeicoliByCittaAndCodicePostale(cityOrigin, postCodeOrigin, veicoliList))
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
