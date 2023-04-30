package com.example.easymove

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)


        firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        val backButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.floatingActionButton)
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
            finish()
        }

        scrittaAccedi.setOnClickListener {
            val intentSignUp = Intent(this, login::class.java)
            startActivity(intentSignUp)
            finish()
        }

        signupbtn.setOnClickListener {

            if (user.getText().toString().isNotEmpty() && pass.getText()
                    .toString().isNotEmpty() && repPass.getText().toString().isNotEmpty()) {

                if(pass.getText().toString() == repPass.getText().toString()){

                    firebaseAuth.createUserWithEmailAndPassword(user.getText().toString(), pass.getText().toString()).addOnCompleteListener{
                        if(it.isSuccessful){
                            val user = hashMapOf(
                                "nome" to nome.getText().toString(),
                                "cognome" to cognome.getText().toString(),
                            )

                            db.collection("users")
                                .add(user)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot added with ID: ${12345}")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }

                            val intent = Intent(this, login::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }

                }
                else{
                    Toast.makeText(this, "Le Password non sono uguali", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this, "Username o Password non inseriti", Toast.LENGTH_SHORT).show()
            }
        }


    }
}