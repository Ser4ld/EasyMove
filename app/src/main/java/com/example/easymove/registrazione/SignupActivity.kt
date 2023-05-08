package com.example.easymove.registrazione

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.easymove.home.HomeActivity
import com.example.easymove.databinding.SignupBinding
import com.example.easymove.login.LoginActivity
import com.example.easymove.R
import com.example.easymove.login.SplashPageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


const val TAG = "FIRESTORE"


class SignupActivity : AppCompatActivity() {

    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var binding: SignupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        fireStoreDatabase = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val backButton =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
                R.id.floatingActionButton
            )


        val email = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)
        val repPass = findViewById<EditText>(R.id.RepeatPassword)

        val scrittaAccedi = findViewById<TextView>(R.id.text_login_2)


        backButton.setOnClickListener {
            val intentBack = Intent(this, SplashPageActivity::class.java)
            startActivity(intentBack)
            finish()
        }

        scrittaAccedi.setOnClickListener {
            val intentSignUp = Intent(this, LoginActivity::class.java)
            startActivity(intentSignUp)
            finish()
        }

        binding!!.signup.setOnClickListener {

            if (email.getText().toString().isNotEmpty() && pass.getText()
                    .toString().isNotEmpty() && repPass.getText().toString().isNotEmpty()
            ) {

                if (pass.getText().toString() == repPass.getText().toString()) {

                    firebaseAuth.createUserWithEmailAndPassword(
                        email.getText().toString(),
                        pass.getText().toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            uploadData(email.getText().toString())
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()

                        }
                    }

                } else {
                    Toast.makeText(this, "Le Password non sono uguali", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this, "Username o Password non inseriti", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }
    private fun uploadData(email: String) {
        val nome = findViewById<EditText>(R.id.Nome)
        val cognome = findViewById<EditText>(R.id.Cognome)


            // create a dummy data
        val hashMap = hashMapOf<String, Any>(
            "name" to nome.getText().toString(),
            "surname" to cognome.getText().toString(),
            "Email" to email,
        )

            // use the add() method to create a document inside users collection
        fireStoreDatabase.collection("users")
            .add(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "Added document with ID ${it.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document $exception")
            }
    }

}
