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

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = UserViewModel()

        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.resetbtn.setOnClickListener(){

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