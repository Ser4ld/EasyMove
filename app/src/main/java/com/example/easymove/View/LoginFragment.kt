package com.example.easymove.View

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.FragmentLoginBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.repository.UserRepository


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var LogViewModel: LoginViewModel
    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogViewModel = LoginViewModel(UserRepository())

        binding.floatingActionButton.setOnClickListener{
            childFragmentManager.popBackStack()
        }
        binding.textSingUp2.setOnClickListener{
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .commit()
        }

        binding.login.setOnClickListener {
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()

            LogViewModel.login(
                email,
                password
            ){ success, message ->
                if (success) {
                    replaceFragment(MainFragment())
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    fun replaceFragment (fragment: Fragment){
        requireFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit()

    }

}