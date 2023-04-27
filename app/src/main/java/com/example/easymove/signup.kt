package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val backbutton= findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton)
        val scrittaAccedi = findViewById<TextView>(R.id.text_login_2)

        backbutton.setOnClickListener{
            val IntentBack= Intent(this, splashpage::class.java)
            startActivity(IntentBack)
        }

        scrittaAccedi.setOnClickListener{
            val IntentSignUp = Intent(this, login::class.java)
            startActivity(IntentSignUp)
        }
    }


}