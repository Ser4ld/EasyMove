package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentRichiesteGuidatoreBinding
import com.example.easymove.databinding.FragmentRichiestePannelloGuidatoreBinding
import com.example.easymove.model.Richiesta
import com.google.android.material.tabs.TabLayout

class RichiestePannelloGuidatoreFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentRichiestePannelloGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel

    private var stato: String = "Attesa"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentRichiestePannelloGuidatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        var richiesteTotali: List<Richiesta>
        var richiesteInAttesa: List<Richiesta>
        var richiesteAccettate: List<Richiesta>
        var richiesteCompletate: List<Richiesta>


        // Osserva i dati dell'utente
        userViewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {

                // Osserva le richieste
                richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiesteList ->
                    if (richiesteList != null) {

                        // Filtra le richieste totali in base all'ID dell'utente e al tipo di utente
                        richiesteTotali = richiestaViewModel.filterRichiesteByUserId(
                            user.id,
                            richiesteList,
                            user.userType
                        )

                        // Filtra le richieste in attesa in base all'ID dell'utente e allo stato
                        richiesteInAttesa = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Attesa",
                            richiesteList,
                            user.userType
                        )
                        // Filtra le richieste accettate in base all'ID dell'utente e allo stato
                        richiesteAccettate = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Accettata",
                            richiesteList,
                            user.userType
                        )

                        // Filtra le richieste completate in base all'ID dell'utente e allo stato
                        richiesteCompletate = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Completata",
                            richiesteList,
                            user.userType
                        )

                        // Aggiorna i dati nella UI con il totale delle richieste per ogni stato
                        binding.textRichiesteTotali2.text =
                            richiestaViewModel.totaleRichieste(richiesteTotali).toString()
                        binding.textRichiesteAttesa2.text =
                            richiestaViewModel.totaleRichieste(richiesteInAttesa).toString()
                        binding.textRichiesteAccettate2.text =
                            richiestaViewModel.totaleRichieste(richiesteAccettate).toString()
                        binding.textRichiesteCompletate2.text= richiestaViewModel.totaleRichieste(richiesteCompletate).toString()
                    }
                }
            }
        }


            if (childFragmentManager.findFragmentById(R.id.frameLayoutRichieste) == null) {
                val initialFragment = RichiesteGuidatoreFragment()
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutRichieste, initialFragment)
                    .commit()
            }

            // Creazione delle tab
            val attesaTab = binding.tabLayout.newTab().setText("IN ATTESA")
            val accettateTab = binding.tabLayout.newTab().setText("ACCETTATE")
            val completateTab = binding.tabLayout.newTab().setText("COMPLETATE")
            val terminateTab = binding.tabLayout.newTab().setText("RIFIUTATE")


            // Aggiunta delle tab al TabLayout
            binding.tabLayout.addTab(attesaTab)
            binding.tabLayout.addTab(accettateTab)
            binding.tabLayout.addTab(completateTab)
            binding.tabLayout.addTab(terminateTab)


            // Listener per le selezioni delle tab
            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    // La tab in cui ci troviamo rappresenta uno stato e
                    // questo viene passato al fragment che rimpiazza il childFragmentManager
                    stato = when (tab.position) {
                        0 -> "Attesa"
                        1 -> "Accettata"
                        2 -> "Completata"
                        3 -> "Rifiutata"
                        else -> throw IllegalArgumentException("Posizione Tab non valida")
                    }

                    // Passa lo stato come argomento al fragment e sostituisci il frameLayoutRichieste
                    bundle.putString("stato", stato)
                    val richiesteGuidatoreFragment = RichiesteGuidatoreFragment()
                    richiesteGuidatoreFragment.arguments = bundle

                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutRichieste, richiesteGuidatoreFragment)
                        .commit()


                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
    }
}