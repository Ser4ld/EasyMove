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


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding
    private var tipoutente: String = "guidatore" //da cambiare e implementare bene quando sistemiamo pattern mvvm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        binding=HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))
        binding.bottomNavigationView.background = null

        binding.addItem.setOnClickListener{
            intentAnnuncio()
        }

        binding.bottomNavigationView.setOnItemSelectedListener {

            /*quando viene cliccato un pulsante della bottomNavigationView, si effettua il controllo dell'id del pulsante e si passa il fragment corrispondente alla funzione ReplaceFragment*/
            when(it.itemId){
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
            }
            true
        }


        if (tipoutente == "guidatore") { //da sistemare bene quando sistemiamo pattern mvvm
            binding.addItem.visibility = View.VISIBLE
        } else {
            binding.addItem.visibility = View.GONE
        }

    }

    private fun intentAnnuncio(){
        val intent = Intent(this, CreaAnnuncioActivity::class.java)
        startActivity(intent)
    }
    /*funziona replaceFragment viene utilizzato per visualizzare un fragment*/
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        // Non fare nulla quando viene premuto il pulsante "Indietro"
        // in modo che l'utente non possa tornare alla schermata di login
    }

}

