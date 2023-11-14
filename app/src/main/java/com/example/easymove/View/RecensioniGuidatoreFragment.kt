package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRecensioni
import com.example.easymove.databinding.FragmentRecensioniGuidatoreBinding

class RecensioniGuidatoreFragment : Fragment() {

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentRecensioniGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var userViewModel: UserViewModel

    // Viene creata un'istanza dell'adapter recensioni
    private lateinit var adapter: MyAdapterRecensioni

    private lateinit var userId:String
    private lateinit var userType: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentRecensioniGuidatoreBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        userViewModel=  ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        // Osserva i dati di tutti gli utenti
        userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                // Osserva i dati dell'utente corrente
                userViewModel.userDataLiveData.observe(viewLifecycleOwner){ user->

                    if( user != null ) {

                        //Ottiene il tipo dell'utente
                        userType= user.userType

                        // Osserva la lista delle recensioni e se questa non è nulla vengono
                        // aggiornate la valutazione media e il totale delle recensioni filtrate per l'utente corrente
                        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner){ recensioniList->
                            if (recensioniList!=null) {
                                binding.ratingBarMedia.rating= recensioneViewModel.mediaRecensioniFiltrate(user.id,recensioniList,userType)
                                binding.textRecensioniTotali2.text= recensioneViewModel.totaleRecensioniFiltrate(user.id,recensioniList,userType).toString()
                            }
                        }
                        userId=user.id

                        // Inizializza l'adapter con i dati dell'utente corrente
                        adapter = MyAdapterRecensioni(ArrayList(), userData, user.userType)
                        binding.recyclerView.adapter = adapter

                    }
                }
            }

        }

        // Listener che permette di tornare alla schermata precedente
        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        // Crea un LinearLayoutManager per la RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        // Osserva la lista delle recensioni dal ViewModel
        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioniList ->
            if (recensioniList.isEmpty()) {
                // Se la lista delle recensioni è vuota, rendi visibile il layout vuoto
                binding.emptyLayout.visibility= View.VISIBLE
            } else {
                // Se la lista delle recensioni non è vuota si va a filtrare la lista di
                // recensioni ottenendo solo quelle che interessano l'utente corrente
                var recensioniFiltrate= recensioneViewModel.filterRecensioneByUserId(userId, recensioniList, userType)

                if (recensioniFiltrate.isEmpty()) {
                    // Se la lista delle recensioni filtrate è vuota, rendi visibile il layout vuoto
                    binding.emptyLayout.visibility= View.VISIBLE
                } else {
                    // Se ci sono recensioni filtrate, rendi invisibile il layout vuoto
                    binding.emptyLayout.visibility= View.GONE
                }

                // Aggiorna l'adapter della RecyclerView con le recensioni filtrate
                adapter.updateRecensioni(recensioniFiltrate)
            }
        }
    }


}