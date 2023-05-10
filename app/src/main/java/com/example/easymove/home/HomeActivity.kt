package com.example.easymove.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.databinding.HomeBinding
import com.example.easymove.profilo.ProfileFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        binding=HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            /*quando viene cliccato un pulsante della bottomNavigationView, si effettua il controllo dell'id del pulsante e si passa il fragment corrispondente alla funzione ReplaceFragment*/
            when(it.itemId){
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
            }
            true
        }

    }

    /*funziona replaceFragment viene utilizzato per visualizzare un fragment*/
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}