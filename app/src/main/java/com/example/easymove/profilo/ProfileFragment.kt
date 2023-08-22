package com.example.easymove.profilo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.View.MainFragment
import com.example.easymove.View.ModificaEmailFragment
import com.example.easymove.ViewModel.ProfileViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentProfileBinding
import com.example.easymove.login.index
import com.example.easymove.View.ResetPasswordFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val db = Firebase.firestore


class ProfileFragment : Fragment(), MessageListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var profileviewModel: ProfileViewModel
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        userViewModel.fetchUserData()

        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                binding.nomeTV.text = userData.name
                binding.cognomeTV.text = userData.surname
                binding.emailTV.text = userData.email
                binding.benvenutoTV.text = "Benvenuto " + userData.name

            }
        }

        binding.modificaPasswordbtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit()
        }



        binding.logout.setOnClickListener {
            logout()
        }



        binding.modificabtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ModificaEmailFragment())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun logout() {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        //imposto lo sfodo del dialog a trasparente per poter applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout effettuato", Toast.LENGTH_SHORT).show()
            val intentLogout = Intent(requireContext(), index::class.java)
            startActivity(intentLogout)
            requireActivity().finish()
            dialog.dismiss()
        }

        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }
        //builder.setMessage("Clicca 'Conferma' per effettuare il logout")

        // Aggiungi i pulsanti "Conferma" e "Annulla"
        /*builder.setPositiveButton("Conferma") { _: DialogInterface, _: Int ->

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout effettuato", Toast.LENGTH_SHORT).show()
            val intentLogout = Intent(requireContext(), index::class.java)
            startActivity(intentLogout)
            requireActivity().finish()

        }*/
        //builder.setNegativeButton("Annulla", null)

        // Mostra il popup
        dialog.show()


    }

    // override metodo presente nell'interfaccia messagelistener per far visualizzare messaggi
    // generati nel viewmodelprofile per segnalare all'utente eventuali errori della modifica della mail
    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}