package com.example.easymove.View

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentCreaRecensioneBinding


class CreaRecensioneFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentCreaRecensioneBinding? = null
    private val binding get() = _binding!!

    private lateinit var  recensioneViewModel : RecensioneViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var userId:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentCreaRecensioneBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dichiarazione delle variabili per l'ID del guidatore e l'ID della richiesta
        var guidatoreId:String
        var richiestaId: String

        // Inizializzazione dei ViewModel necessari
        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        // Osserva i LiveData dell'utente corrente per ottenere l'ID dell'utente
        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                userId= userData.id
            }
        }

        // Recupera gli argomenti passati al fragment, se presenti
        val argument=arguments
        if (argument != null){
            guidatoreId = argument.getString("guidatoreId").toString()
            richiestaId = argument.getString("richiestaId").toString()
        } else{
            // Imposta valori predefiniti se gli argomenti non sono disponibili
            guidatoreId="Id Guidatore non disponibile"
            richiestaId="Id Richiesta non disponibile"
        }


        // Gestisce il clic del pulsante per inviare una recensione
        binding.btnInviaRecensione.setOnClickListener {

            // viene richiamato il metodo crearecensione che permette
            // di creare e memorizzare la recensione
            recensioneViewModel.creaRecensione(
                richiestaId,
                userId,
                guidatoreId,
                binding.ratingBar.rating.toString(),
                binding.etDescrizione.text.toString()
            )

            { success, message ->
                if (success) {
                    // in caso di successo torna alla schermata precedente
                    hideKeyboard()
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Gestisce il clic del pulsante per tornare alla schermata precedente
        binding.floatingActionButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        // Aggiunge un [TextWatcher] all'EditText per la descrizione, che aggiorna la
        // visualizzazione del conteggio dei caratteri durante la digitazione
       binding.etDescrizione.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // Aggiorna il conteggio dei caratteri durante la digitazione
                val currentLength = s?.length ?: 0
                binding.characterCountTextView.text = "$currentLength/300"

            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    // nasconde la tastiera
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}