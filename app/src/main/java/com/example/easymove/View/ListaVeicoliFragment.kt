package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentListaVeicoliBinding
import com.example.easymove.model.Richiesta
import com.google.firebase.auth.FirebaseAuth

class ListaVeicoliFragment : Fragment() {

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentListaVeicoliBinding? = null
    private val binding get() = _binding!!

    private lateinit var cityOrigin: String
    private lateinit var postCodeOrigin: String
    private lateinit var distance: String

    private lateinit var origin: String
    private lateinit var destination: String


    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel

    // Viene creata un'istanza dell'adapter veicoli
    private lateinit var adapter: MyAdapterVeicoli

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentListaVeicoliBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione dei ViewModel necessari
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        // Imposta il comportamento del pulsante a comparsa per tornare al frammento precedente nella pila.
        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Configurazione del LayoutManager per la RecyclerView.
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager


        // Recupero degli argomenti passati al frammento (distanza e dettagli dell'origine).
        val argument=arguments
        if (argument != null){
            cityOrigin = argument.getString("originCity").toString()
            postCodeOrigin = argument.getString("originPostCode").toString()
            origin = argument.getString("origin").toString()
            destination = argument.getString("destination").toString()
            distance = argument.get("distance").toString()
        }

        // Osserva i dati degli utenti per popolare la RecyclerView nel momento in cui sono disponibili.
        userViewModel.allUsersLiveData.observe(viewLifecycleOwner){userList ->
            if(userList != null){
                // Inizializzazione dell'AdapterVeicoli per la RecyclerView
                adapter = MyAdapterVeicoli(veicoliViewModel,userViewModel,richiestaViewModel,ArrayList(),emptyList(),distance, userList)
                binding.recyclerView.adapter = adapter
            }
        }

        // Osserva i dati dei veicoli per popolare la RecyclerView quando sono disponibili.
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
            if (veicoliList.isEmpty()) {

                // Mostra un messaggio di errore se la lista dei veicoli è vuota.
                Toast.makeText(requireContext(), "Si è verificato un errore", Toast.LENGTH_SHORT).show()
            } else {
                // Filtra i veicoli in base alla città e al codice postale dell'origine.
                var veicoliFiltrati = veicoliViewModel.filterVeicoliByCittaAndCodicePostale(cityOrigin, postCodeOrigin, veicoliList)

                // Ordina la lista dei veicoli per modello
                val sortedVeicoliList = veicoliFiltrati.sortedBy { it.modello }

                // Osserva gli eventi di clic sulla richiesta.
                veicoliViewModel.richiestaClickedEvent.observe(viewLifecycleOwner) { position ->
                    if (position != -1) {

                        // Ottieni il veicolo selezionato dalla lista ordinata.
                        val selectedVehicle = sortedVeicoliList[position]

                        // Crea un bundle di dati da passare al fragment successivo.
                        val bundle = Bundle()
                        bundle.putString("modello", selectedVehicle.modello)
                        bundle.putString("targa", selectedVehicle.targa)
                        bundle.putString("capienza", selectedVehicle.capienza)
                        bundle.putString("id_guidatore", selectedVehicle.id)
                        bundle.putString("tariffaKm", selectedVehicle.tariffakm)
                        bundle.putString("distance", distance)
                        bundle.putString("destination", destination)
                        bundle.putString("origin", origin)

                        // Resetta il valore dell'evento di clic per evitare la persistenza dell'evento.
                        veicoliViewModel.resetRichiestaClickedEvent()

                        // Crea ed esegui la transazione del fragment InoltraRichiestaFragment.
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

        // Osserva nuovamente i dati dei veicoli per aggiornare la RecyclerView
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
            if (veicoliList.isEmpty()) {
                // Se la lista dei veicoli è vuota, mostra il layout vuoto
                binding.emptyLayout.visibility = View.VISIBLE
            } else {
                // Filtra i veicoli in base alla città e al codice postale di origine
                var veicoliFiltrati = veicoliViewModel.filterVeicoliByCittaAndCodicePostale(cityOrigin, postCodeOrigin, veicoliList)

                // Aggiorna la visibilità del layout vuoto in base alla lista filtrata.
                if (veicoliFiltrati.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                } else {
                    binding.emptyLayout.visibility = View.GONE
                }
                // Aggiorna i dati nella RecyclerView con la lista filtrata.
                adapter.updateData(veicoliFiltrati)
            }
        }
    }


    // Pulisce il binding quando la vista viene distrutta per evitare memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
