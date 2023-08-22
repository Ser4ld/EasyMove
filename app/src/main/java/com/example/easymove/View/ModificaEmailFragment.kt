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
    private var _binding: FragmentModificaEmailBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var current_email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentModificaEmailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                current_email = userData.email

            }
        }


        binding.resetbtn.setOnClickListener{
            val new_mail = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()

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