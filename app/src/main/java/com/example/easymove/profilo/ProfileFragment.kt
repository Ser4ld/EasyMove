package com.example.easymove.profilo

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.easymove.R
import com.example.easymove.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val db = Firebase.firestore


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fireStoreDatabase: FirebaseFirestore
    val prova = ProfileRepository()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding= FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmailTask = prova.getUserEmail()
        val userNameTask = prova.getUserName()
        val userSurnameTask = prova.getUserSurname()

        userEmailTask.addOnSuccessListener { email ->
            binding.emailTV.text = email

        }

        userNameTask.addOnSuccessListener { name ->
            binding.nomeTV.text = name
            binding.benvenutoTV.text = "Benvenuto "+ name
        }

        userSurnameTask.addOnSuccessListener { surname ->
            binding.cognomeTV.text = surname
        }

    }

    private fun showEditNamePopup(userId: String) {
        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())

        // Imposta la vista del popup
        val view = layoutInflater.inflate(R.layout.popup_edit_data, null)
        builder.setView(view)

        // Trova i riferimenti all' EditText
        val editTextName = view.findViewById<EditText>(R.id.edit_text_email)

        // Aggiungi i pulsanti "OK" e "Annulla"
        builder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            // Leggi i valori dai campi EditText
            val newEmail = editTextName.text.toString()
            // Aggiorna l'email nel Firestore database
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocRef.update("Email", newEmail)
                .addOnSuccessListener {
                    Log.d(TAG, "Email aggiornata nel Firestore database")
                }
                .addOnFailureListener { e ->
                    Log.w(
                        TAG,
                        "Errore durante l'aggiornamento dell'email nel Firestore database",
                        e
                    )
                }
            binding.emailTV.text = newEmail
            // Aggiorna l'email nel Firestore Authentication
            val user = FirebaseAuth.getInstance().currentUser
            user?.updateEmail(newEmail)?.addOnSuccessListener { Log.d(TAG, "Email aggiornata nel Firestore Authentication") }?.addOnFailureListener { e -> Log.w(TAG, "Errore durante l'aggiornamento dell'email nel Firestore Authentication", e) }
        }
        builder.setNegativeButton("Annulla", null)

        // Mostra il popup
        builder.show()

    }

}