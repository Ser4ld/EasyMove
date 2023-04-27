package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /** window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN*/ /**toglie la barra viola ma la fa nera*/
        setContentView(R.layout.login)

        val backbutton= findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton)
        val scrittaRegistrati = findViewById<TextView>(R.id.text_sing_up_2)
        val loginbtn = findViewById<Button>(R.id.login)
        val user = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)


       backbutton.setOnClickListener{
            val IntentBack= Intent(this, splashpage::class.java)
            startActivity(IntentBack)
        }
        scrittaRegistrati.setOnClickListener{
            val IntentSignUp = Intent(this, signup::class.java)
            startActivity(IntentSignUp)
        }

        loginbtn.setOnClickListener {

            if (user.getText().toString().isNotEmpty() && pass.getText().toString().isNotEmpty()) {
                if (user.getText().toString().equals("admin") && pass.getText().toString()
                        .equals("pass")
                ) {
                    val Intent = Intent(this, signup::class.java)
                    startActivity(Intent)
                } else {
                    Toast.makeText(this, "Username o Password errati", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Username o Password non inseriti", Toast.LENGTH_SHORT).show()
            }

        }

    }
}