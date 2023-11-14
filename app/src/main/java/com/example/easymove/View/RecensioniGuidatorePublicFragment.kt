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
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRecensioni
import com.example.easymove.databinding.FragmentRecensioniGuidatoreBinding
import com.example.easymove.databinding.FragmentRecensioniGuidatorePublicBinding
import com.example.easymove.model.User


class RecensioniGuidatorePublicFragment : Fragment() {
    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var userViewModel: UserViewModel

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentRecensioniGuidatorePublicBinding? = null
    private val binding get() = _binding!!

    //Dichiarazione adapter
    private lateinit var adapter: MyAdapterRecensioni

    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentRecensioniGuidatorePublicBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione Viewmodel
        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        userViewModel=  ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        // Imposta il layout manager per la RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager


        // Gestisce il click sul pulsante di navigazione per tornare indietro
        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Osserva la lista delle recensioni
        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioneList ->

            // Osserva la lista completa degli utenti
            userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->

                // Osserva i dati dell'utente attualmente connesso
                userViewModel.userDataLiveData.observe(viewLifecycleOwner){userData->
                    if(userData!=null) {

                        // Controlla che la lista degli utenti sia disponibile
                        if (userList != null) {
                            val argument = arguments
                            if (argument != null) {

                                // Ottiene l'ID del guidatore dagli arguments passati dal fragment
                                // precedentemente visualizzato
                                val guidatoreId = argument.getString("idguidatore").toString()

                                // Filtra la lista degli utenti secondo l'id del guidatore
                                user = userViewModel.FilterListById(guidatoreId, userList)!!

                                if (user != null) {
                                    // Inizializza l'adapter delle recensioni
                                    adapter = MyAdapterRecensioni(ArrayList(), userList,"guidatore")

                                    // Collega l'adapter alla RecyclerView
                                    binding.recyclerView.adapter = adapter

                                    // Controlla se la lista delle recensioni è vuota,
                                    // se lo fosse rende visibile il layout vuoto
                                    if (recensioneList.isEmpty()) {
                                        binding.emptyLayout.visibility = View.VISIBLE
                                    } else {
                                        // Filtra le recensioni in base all'ID dell'utente guidatore
                                        var recensioniFiltrate = recensioneViewModel.filterRecensioneByUserId(
                                            user.id,
                                            recensioneList,
                                            user.userType )

                                        // Controlla se la lista filtrata è vuota
                                        // e setta la visibilità del layout vuoto
                                        if (recensioniFiltrate.isEmpty()) {
                                            binding.emptyLayout.visibility = View.VISIBLE
                                        } else {
                                            binding.emptyLayout.visibility = View.GONE
                                        }

                                        // Aggiorna la RecyclerView con le recensioni filtrate
                                        adapter.updateRecensioni(recensioniFiltrate)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    // richiamato per distruggere la vista
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}