package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        firebaseAuth = FirebaseAuth.getInstance()


        val backbutton= findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton)
        val scrittaRegistrati = findViewById<TextView>(R.id.text_sing_up_2)
        val passwordDimenticata = findViewById<TextView>(R.id.passwordDimenticata)
        val loginbtn = findViewById<Button>(R.id.login)
        val user = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)


        passwordDimenticata.setOnClickListener{
            val intentPassDimenticata= Intent(this, resetpassword::class.java)
            startActivity(intentPassDimenticata)
        }
       backbutton.setOnClickListener{
            val intentBack= Intent(this, firstpage::class.java)
            startActivity(intentBack)
        }
        scrittaRegistrati.setOnClickListener{
            val intentSignUp = Intent(this, SignupActivity::class.java)
            startActivity(intentSignUp)
        }

        loginbtn.setOnClickListener {

            if (user.getText().toString().isNotEmpty() && pass.getText().toString().isNotEmpty()) {


                firebaseAuth.signInWithEmailAndPassword(user.getText().toString(), pass.getText().toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, home::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }
            else{
                Toast.makeText(this, "Username o Password non inseriti", Toast.LENGTH_SHORT).show()
            }
        }

    }
}