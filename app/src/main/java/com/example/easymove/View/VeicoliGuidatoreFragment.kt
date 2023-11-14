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

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentVeicoliGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel

    //dichiarazione adapter dei veicoli
    private lateinit var adapter: MyAdapterVeicoli
    private lateinit var utenteId:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentVeicoliGuidatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    // Segnala all'analizzatore statico di Android di ignorare gli avvisi relativi alla rilevazione sospetta di indentazione
    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Inizializzazione dei ViewModel
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        // Avvio del listener per i veicoli
        veicoliViewModel.startVeicoliListener()


        // Gestione del click del FloatingActionButton per tornare indietro nella navigazione
        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // viene osservato il live data contenente i dati dell'utente corrente per andare a prendere il suo id
        userViewModel.userDataLiveData.observe(viewLifecycleOwner){currentUser->
            if (currentUser != null) {
                utenteId = currentUser.id
            }
        }

        // Configurazione della RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        // viene osservato il live data contenente i dati delle richieste
        richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner){richiesteList ->
            if(richiesteList != null)

            // Creazione di un nuovo adapter personalizzato per veicoli
            adapter = MyAdapterVeicoli(veicoliViewModel,userViewModel,richiestaViewModel,ArrayList(),richiesteList,"", null)
            binding.recyclerView.adapter = adapter

        }

        // viene osservato il live data contenente i dati dei veicoli
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->

            // Filtraggio dei veicoli secondo l'id dell'utente che li possiede
            var veicoliFiltrati= veicoliViewModel.filterVeicoliByUserId(utenteId, veicoliList)

            // Controllo se la lista dei veicoli filtrati è vuota se lo è viene ftto vedere il
            // layout vuoto se la lista non è vuota viene reso invisibile il layout vuoto
            if(veicoliFiltrati.isEmpty()){
                binding.emptyLayout.visibility = View.VISIBLE
            } else {
                binding.emptyLayout.visibility = View.GONE
            }

            // Aggiornamento dell'Adapter con la lista filtrata dei veicoli
            adapter.updateData(veicoliFiltrati)

        }
    }
}