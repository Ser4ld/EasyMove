package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentResetPasswordBinding


class ResetPasswordFragment : Fragment() {

    // Binding per manipolare gli oggetti del layout
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crea un'istanza di UserViewModel
        userViewModel = UserViewModel()

        // Gestisce il clic sul pulsante per tornare al fragment precedente
        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        // Gestisce il clic sul pulsante di reset della password
        binding.resetbtn.setOnClickListener(){

            // Chiama il metodo sendPasswordResetEmail del UserViewModel per inviare un'email di reset della password
            // passandogli la mail inserita all'interno dei parametri
            userViewModel.sendPasswordResetEmail(
                binding.textEmail.text.toString(),
                {
                    Toast.makeText(requireContext(), "Controlla la tua Email", Toast.LENGTH_SHORT).show()
                },
                {
                    Toast.makeText(requireContext(), "La Email inserita non è corretta", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}