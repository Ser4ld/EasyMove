package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.databinding.FragmentIndexBinding
import com.example.easymove.View.LoginFragment
import com.example.easymove.View.SignupFragment


class IndexFragment : Fragment() {
    // Variabile per il binding del fragment Index
    private var _binding: FragmentIndexBinding? = null

    // Proprietà per accedere al binding in modo sicuro non nullo
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizzato per creare l'oggetto di binding associato al layout del fragment
        _binding = FragmentIndexBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposta il listener per il pulsante di accesso, al click si passa alla visualizzazione
        // del fragment LoginFragment
        binding.loginButtonIndex.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        // Imposta il listener per il pulsante di registrazione, al click si passa alla visualizzazione
        // del fragment SignupFragment
        binding.signupButtonIndex.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SignupFragment())
                .addToBackStack(null)
                .commit()
        }
    }

}