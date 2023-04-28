package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val backButton =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton)
        val signupbtn = findViewById<Button>(R.id.signup)

        val nome = findViewById<EditText>(R.id.Nome)
        val cognome = findViewById<EditText>(R.id.Cognome)
        val user = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)
        val repPass = findViewById<EditText>(R.id.RepeatPassword)

        val scrittaAccedi = findViewById<TextView>(R.id.text_login_2)


        backButton.setOnClickListener {
            val intentBack = Intent(this, firstpage::class.java)
            startActivity(intentBack)
        }

        scrittaAccedi.setOnClickListener {
            val intentSignUp = Intent(this, login::class.java)
            startActivity(intentSignUp)
        }

        signupbtn.setOnClickListener {

            if (nome.getText().toString().isNotEmpty() && cognome.getText().toString()
                    .isNotEmpty() && user.getText().toString().isNotEmpty() && pass.getText()
                    .toString().isNotEmpty() && repPass.getText().toString().isNotEmpty())
            {
                val intentHome = Intent(this, signup::class.java)
                startActivity(intentHome) /** modificare firstpage con home*/

            } else {
                Toast.makeText(this, "Alcuni dati mancanti", Toast.LENGTH_SHORT).show()
            }

        }


    }
}