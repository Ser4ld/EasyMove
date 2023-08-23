package com.example.easymove.View

import android.content.Intent
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

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignupViewModel
    private lateinit var userRepository: UserRepository
    private var tipoutente = "consumatore" // Esempio fisso, potrebbe variare a seconda del toggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = SignupViewModel(UserRepository())

        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.textLogin2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }


        binding.signup.setOnClickListener {
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()
            val nome = binding.Nome.text.toString()
            val cognome = binding.Cognome.text.toString()
            val repeatPassword = binding.RepeatPassword.text.toString()


            viewModel.signUp(
                email,
                password,
                repeatPassword,
                nome,
                cognome,
                tipoutente
            ) { success, message ->
                if (success) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MainFragment())
                        .commit()
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.checkguidatore.setOnCheckedChangeListener { _, isChecked ->
            tipoutente = if (isChecked) {
                "guidatore"
            } else {
                "consumatore"
            }
        }
    }
}