package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentVeicoliGuidatorePublicBinding
import com.example.easymove.model.User

class VeicoliGuidatorePublicFragment : Fragment() {

    // Variabile per il binding del fragment
    private var _binding: FragmentVeicoliGuidatorePublicBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var user: User

    // Dichiarazione adapter
    private lateinit var adapter: MyAdapterVeicoli

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Utilizzato per creare l'oggetto di binding associato al layout del fragment
        _binding = FragmentVeicoliGuidatorePublicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza i ViewModel necessari per gestire i dati degli utenti, dei veicoli e delle richieste.
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        richiestaViewModel =
            ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        // Configura il layout manager e l'adapter per la RecyclerView dei veicoli.
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager


        // torna all'ultima schermata visualizzata
        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        // Osserva i dati degli utenti e, quando disponibili, recupera l'ID del guidatore dal bundle degli argomenti.
        userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->
            userViewModel.userDataLiveData.observe(viewLifecycleOwner) { userData ->
                if (userData != null && userList != null) {
                    val argument = arguments
                    if (argument != null) {

                        // se gli argomenti risultano non nulli allora viene
                        // salvato il loro valore nella variabile guidatoreId
                        val guidatoreId = argument.getString("idguidatore").toString()

                        // otteniamo lo user fitrando la lista di tutti gli utenti secondo l'id del guidatore
                        user = userViewModel.FilterListById(guidatoreId, userList)!!

                        if (user != null) {

                            // Inizializza e imposta l'adapter per la RecyclerView dei veicoli
                            adapter = MyAdapterVeicoli(
                                veicoliViewModel,
                                userViewModel,
                                richiestaViewModel,
                                ArrayList(),
                                emptyList(),
                                "",
                                userList
                            )
                            binding.recyclerView.adapter = adapter

                            // viene osservata la lista di veicoli
                            veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
                                if (veicoliList.isEmpty()) {

                                    // Rende visibile il layout vuoto se la lista di veicoli è vuota
                                    binding.emptyLayout.visibility = View.VISIBLE
                                } else {

                                    // se la lista dei veicoli non è vuota vado a filtrarla
                                    // secondo l'id del guidatore
                                    val recensioniFiltrate =
                                        veicoliViewModel.filterVeicoliByUserId(
                                            guidatoreId,
                                            veicoliList
                                        )

                                    if (recensioniFiltrate.isEmpty()) {

                                        // la lista di recensioni filtrate è vuota allora
                                        // verrà visualizzato il layout vuoto
                                        binding.emptyLayout.visibility = View.VISIBLE

                                    } else {
                                        // Altrimenti impostiamo il layout vuoto non visibile
                                        binding.emptyLayout.visibility = View.GONE
                                    }

                                    // aggirna i dati della reycle view con i la lista dei veicoli filtrata
                                    adapter.updateData(recensioniFiltrate)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Chiamato quando la vista associata al fragment viene distrutta.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
