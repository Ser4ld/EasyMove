package com.example.easymove.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.databinding.HomeBinding
import com.example.easymove.profilo.ProfileFragment
import android.content.Intent
import android.util.Log
import android.view.View
import com.example.easymove.CreaAnnuncioActivity
import com.example.easymove.databinding.FragmentProfileBinding
import com.example.easymove.profilo.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding
    private lateinit var firestore: FirebaseFirestore
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        replaceFragment(HomeFragment())

        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))
        binding.bottomNavigationView.background = null

        binding.addItem.setOnClickListener {
            intentAnnuncio()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
            }
            true
        }

        // Qui chiamiamo la funzione per ottenere il valore di "tipoutente"
        fetchAndSetTipoutente()
    }
    fun getUserId(): String {
        val userId = user?.uid
        return userId.toString()
    }

    private fun fetchAndSetTipoutente() {
        val userId = getUserId() // Sostituisci con l'ID dell'utente corrente

        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val tipoutente = documentSnapshot.getString("tipoutente")
                    if (tipoutente == "guidatore") {
                        binding.addItem.visibility = View.VISIBLE
                    } else {
                        binding.addItem.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Errore durante la lettura dal Firestore", exception)
            }
    }

    private fun intentAnnuncio() {
        val intent = Intent(this, CreaAnnuncioActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        // Non fare nulla quando viene premuto il pulsante "Indietro"
        // in modo che l'utente non possa tornare alla schermata di login
    }
}
