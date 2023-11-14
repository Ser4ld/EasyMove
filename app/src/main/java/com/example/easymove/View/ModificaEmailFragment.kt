package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentIndexBinding
import com.example.easymove.databinding.FragmentModificaEmailBinding

class ModificaEmailFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentModificaEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var current_email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentModificaEmailBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ottenere l'istanza del ViewModel associata all'attività genitore
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        // controlla l'evento di click del bottone floatingActionButton per tornare alla schermata precedente
        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }


        // Si va ad osservare il livedata dell'utente corrente per ottenere la sua mail
        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            // Verificare se sono presenti dati dell'utente
            if (userData != null) {
                current_email = userData.email

            }
        }


        // Gestore evento di click del pulsante di reset dell'email
        binding.resetbtn.setOnClickListener{

            // Vengono salvate la nuova mail e la password dell'account scritte nelle edit text dall'utente
            val new_mail = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()

            // Viene richiamato il metodo di UserViewModel per la modifica dell'email con riautenticazione
            userViewModel.modifyMailWithReauthentication(
                current_email, new_mail, password,
            ) { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), "Email aggiornata con successo", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}