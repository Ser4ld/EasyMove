package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.View.MainFragment
import com.example.easymove.View.SignupFragment
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.FragmentLoginBinding
import com.example.easymove.repository.UserRepository


class LoginFragment : Fragment() {

    // Binding per manipolare gli oggetti del layout
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var LogViewModel: LoginViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Utilizza il binding per associare il layout del fragment di login al codice
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment di login
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Crea un'istanza del LoginViewModel associato alla UserRepository
        LogViewModel = LoginViewModel(UserRepository())

        // Gestisce il clic sul pulsante per tornare al fragment precedente
        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        // Gestisce il clic sul testo "Sign Up" per navigare al fragment di registrazione
        binding.textSingUp2.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .commit()
        }

        // Gestisce il clic sul pulsante di login
        binding.login.setOnClickListener {

            // Recupera sia la mail che la password inserite nelle edit text dall'utente
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()

            // Richiama il metodo login del loginviewmodel
            LogViewModel.login(
                email,
                password
            ){ success, message ->
                // Se il login ha successo, sostituisci il fragment corrente con il MainFragment
                // Altrimenti, mostra un Toast con il messaggio di errore
                if (success) {
                    replaceFragment(MainFragment())
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Gestisce il clic sul testo "Password Dimenticata" per navigare al fragment di reset password
        binding.passwordDimenticata.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ResetPasswordFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    // Metodo privato per sostituire il fragment corrente con un nuovo fragment
    private fun replaceFragment (fragment: Fragment){
        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

    }

}
