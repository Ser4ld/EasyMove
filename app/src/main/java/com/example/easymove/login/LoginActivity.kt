package com.example.easymove.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easymove.home.HomeActivity
import com.example.easymove.R
import com.example.easymove.databinding.SignupBinding
import com.example.easymove.profilo.ProfileFragment
import com.example.easymove.registrazione.SignupActivity
import com.google.firebase.auth.FirebaseAuth


open class LoginActivity : AppCompatActivity() {

    val loginRepository = LoginRepository(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val backbutton= findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.floatingActionButton
        )
        val scrittaRegistrati = findViewById<TextView>(R.id.text_sing_up_2)
        val passwordDimenticata = findViewById<TextView>(R.id.passwordDimenticata)
        val loginbtn = findViewById<Button>(R.id.login)
        val user = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)


        /*intent per passare alle schermate di RecuperoPassword, index (cliccando il backbutton) e pagina di registrazione */

        passwordDimenticata.setOnClickListener{
            val intentPassDimenticata= Intent(this, ResetPasswordActivity::class.java)
            startActivity(intentPassDimenticata)
        }
       backbutton.setOnClickListener{
            val intentBack= Intent(this, index::class.java)
            startActivity(intentBack)
        }
        scrittaRegistrati.setOnClickListener{
            val intentSignUp = Intent(this, SignupActivity::class.java)
            startActivity(intentSignUp)
        }

        loginbtn.setOnClickListener {

            if (user.getText().toString().isNotEmpty() && pass.getText().toString().isNotEmpty()) {
                loginRepository.autenticazione(user.getText().toString(), pass.getText().toString())
            }
            else{
                Toast.makeText(this, "Email o Password non inseriti", Toast.LENGTH_SHORT).show()
            }
        }

    }

}