package com.example.easymove.profilo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.ProfileViewModel
import com.example.easymove.databinding.FragmentProfileBinding
import com.example.easymove.login.ResetPasswordActivity
import com.example.easymove.login.index
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val db = Firebase.firestore


class ProfileFragment : Fragment(), MessageListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var viewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Richiamo la funzione fetchData() per ottenere i dati desiderati
        viewModel.fetchData()

        // Collega il LiveData dei dati dell'utente al layout tramite databinding
        viewModel.getData()["name"]?.observe(viewLifecycleOwner, Observer { name ->
            binding.nomeTV.text = name
        })

        viewModel.getData()["surname"]?.observe(viewLifecycleOwner, Observer { surname ->
            binding.cognomeTV.text = surname
        })

        viewModel.getData()["email"]?.observe(viewLifecycleOwner, Observer { email ->
            binding.emailTV.text = email
        })

        binding.modificaPasswordbtn.setOnClickListener {
            val intentModificaPass = Intent(requireActivity(), ResetPasswordActivity::class.java)
            startActivity(intentModificaPass)
        }

        binding.logout.setOnClickListener {
            logout()
        }

        binding.modificabtn.setOnClickListener {
            val idData = viewModel.getData()["id"]?.value
            if (idData != null) {
                showEditNamePopup(idData.toString())
            }
        }

        return binding.root
    }



    private fun showEditNamePopup(userId: String) {
        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())

        // Imposta la vista del popup
        val view = layoutInflater.inflate(R.layout.popup_modifica_email, null)
        builder.setView(view)

        // Trova i riferimenti all'EditText
        val editTextEmail = view.findViewById<EditText>(R.id.edit_text_email)

        // Aggiungi i pulsanti "OK" e "Annulla"
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->

            // Richiedi all'utente di rieffettuare l'accesso con la password attuale
            showPasswordInputDialog { password ->
                // Callback chiamata dopo aver ottenuto la password dall'utente
                if(editTextEmail.text.toString().isEmpty()){
                    dialog.dismiss()
                }
                else{
                    // Callback chiamata dopo aver ottenuto la password dall'utente
                    viewModel.updateEmailWithReauthentication("Email", editTextEmail.text.toString(), password, this)
                    viewModel.fetchData()
                }
            }
        }
        builder.setNegativeButton("Annulla", null)

        // Mostra il popup
        builder.show()
    }

    private fun showPasswordInputDialog(callback: (password: String) -> Unit) {
        val passwordInputDialog = AlertDialog.Builder(requireContext())

        // Imposta la vista del popup
        val view = layoutInflater.inflate(R.layout.popup_password_modifica_email, null)
        val input = view.findViewById<EditText>(R.id.edit_text_email)
        passwordInputDialog.setView(view)

        passwordInputDialog.setPositiveButton("OK") { dialog, _ ->
            val password = input.text.toString()
            callback(password)
            dialog.dismiss()
        }

        passwordInputDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        passwordInputDialog.show()
    }

    private fun logout() {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage("Clicca 'Conferma' per effettuare il logout")

        // Aggiungi i pulsanti "Conferma" e "Annulla"
        builder.setPositiveButton("Conferma") { _: DialogInterface, _: Int ->

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout effettuato", Toast.LENGTH_SHORT).show()
            val intentLogout = Intent(requireContext(), index::class.java)
            startActivity(intentLogout)
            requireActivity().finish()

        }
        builder.setNegativeButton("Annulla", null)

        // Mostra il popup
        builder.show()


    }

    // override metodo presente nell'interfaccia messagelistener per far visualizzare messaggi
    // generati nel viewmodelprofile per segnalare all'utente eventuali errori della modifica della mail
    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}