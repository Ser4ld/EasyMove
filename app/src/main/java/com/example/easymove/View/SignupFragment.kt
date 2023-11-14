package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.easymove.R
import com.example.easymove.ViewModel.SignupViewModel
import com.example.easymove.databinding.FragmentSignupBinding
import com.example.easymove.repository.UserRepository


class SignupFragment : Fragment() {

    // Binding per manipolare gli oggetti del layout
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignupViewModel
    private var tipoutente = "consumatore"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crea un'istanza del SignupViewModel associato a un UserRepository
        viewModel = SignupViewModel(UserRepository())

        // Gestisce il clic sul pulsante per tornare al fragment precedente
        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Gestisce il clic sul testo "Login" per navigare al fragment di login
        binding.textLogin2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }

        // Gestisce il clic sul pulsante di registrazione
        binding.signup.setOnClickListener {
            // Ottieni i dati inseriti dall'utente nei campi di registrazione
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()
            val nome = binding.Nome.text.toString()
            val cognome = binding.Cognome.text.toString()
            val repeatPassword = binding.RepeatPassword.text.toString()

            // Esegui la registrazione utilizzando il SignupViewModel
            viewModel.signUp(
                email,
                password,
                repeatPassword,
                nome,
                cognome,
                tipoutente
            ) { success, message ->

                // Se la registrazione ha successo, sostituisci il fragment corrente con il MainFragment
                // Altrimenti, mostra un Toast con il messaggio di errore
                if (success) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MainFragment())
                        .commit()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }


        }

        // Gestisce il cambiamento dello stato del CheckBox "Guidatore/Consumatore"
        // se la checkbox risulta spuntata allora l'utente è un guidatore viceversa è un consumatore
        binding.checkguidatore.setOnCheckedChangeListener { _, isChecked ->
            tipoutente = if (isChecked) {
                "guidatore"
            } else {
                "consumatore"
            }
        }
    }
}