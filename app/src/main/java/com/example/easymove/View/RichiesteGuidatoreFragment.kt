package com.example.easymove.View

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRichieste
import com.example.easymove.databinding.FragmentRichiesteGuidatoreBinding
import com.example.easymove.model.Richiesta


class RichiesteGuidatoreFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentRichiesteGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel

    // inizializzazione adapter delle richieste
    private lateinit var adapter: MyAdapterRichieste

    private lateinit var userId:String
    private lateinit var stato:String
    private lateinit var userType: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentRichiesteGuidatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera l'argomento passato dal precedente fragment
        var argument=arguments
        if (argument != null ){
            stato= argument.getString("stato").toString()
        }
        // se l'argomento è nullo imposta lo stato in attesa
        else stato="Attesa"

        // Inizializza i ViewModel necessari
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)

        // Inizializza i ViewModel necessari
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewRichiesta.layoutManager = layoutManager

        // Osserva i LiveData del ViewModel dei veicoli per gli aggiornamenti
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner){veicoliList->

            // Osserva i LiveData di tutti gli utenti per gli aggiornamenti
            userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userData ->
                if (userData != null) {

                    // Osserva i LiveData dell'utente corrente per gli aggiornamenti
                    userViewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
                        if (user != null) {
                            // Imposta il tipo di utente e l'ID dell'utente corrente
                            userType= user.userType
                            userId = user.id

                            // Inizializza e imposta l'adapter per il RecyclerView
                            adapter = MyAdapterRichieste(ArrayList(),veicoliList, userData,userType,recensioneViewModel,richiestaViewModel, userViewModel, veicoliViewModel)
                            binding.recyclerViewRichiesta.adapter = adapter
                        }
                    }
                }
        }


            // Osserva i LiveData delle richieste per gli aggiornamenti
            richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiestaList ->
                if (richiestaList.isEmpty()) {
                    // Mostra il layout vuoto se la lista delle richieste è vuota
                    binding.emptyLayout.visibility=VISIBLE
                } else {

                    // Filtra le richieste in base all'ID dell'utente e allo stato
                    var richiesteFiltrate = ArrayList(richiestaViewModel.filterRichiesteByUserIdAndStato(userId, stato, richiestaList, userType))

                    if (richiesteFiltrate.isEmpty()) {

                        // Mostra il layout vuoto se la lista delle richieste filtrate è vuota
                        binding.emptyLayout.visibility=VISIBLE
                    } else {

                        // Nasconde il layout vuoto se ci sono richieste da mostrare
                        binding.emptyLayout.visibility=GONE
                    }

                    // Aggiorna l'adapter con le richieste filtrate
                    adapter.updateRichieste(richiesteFiltrate)

                }
            }
        }
    }
}