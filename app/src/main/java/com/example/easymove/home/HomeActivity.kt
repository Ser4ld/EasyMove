package com.example.easymove.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.databinding.HomeBinding
import com.example.easymove.profilo.ProfileFragment
import android.content.Intent
import android.view.View
import com.example.easymove.CreaAnnuncioActivity
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.model.User
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: HomeBinding
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = User(FirebaseFirestore.getInstance()) // Inizializza la proprietà user
        replaceFragment(HomeFragment())
        homeViewModel = HomeViewModel()


        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))
        binding.bottomNavigationView.background = null

        binding.addItem.setOnClickListener {
            val intent = Intent(this, CreaAnnuncioActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
            }
            true
        }

        // Qui chiamiamo la funzione per ottenere il valore di "tipoutente"
        homeViewModel.fetchAndSetTipoutente(user.getUserId()) { isGuidatore ->
            if (isGuidatore) {
                binding.addItem.visibility = View.VISIBLE
            } else {
                binding.addItem.visibility = View.GONE
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
    override fun onBackPressed() {
        // Non fa nulla quando viene premuto il pulsante "Indietro"
    }
}
