package com.example.easymove.registrazione

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.example.easymove.databinding.SignupBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.login.LoginActivity
import com.example.easymove.login.index
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


const val TAG = "FIRESTORE"

class SignupActivity : AppCompatActivity() {

    private lateinit var fireStoreDatabase: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: SignupBinding
    private var tipoutente: String = "consumatore"
    private lateinit var nome: String
    private lateinit var cognome: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreDatabase = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.floatingActionButton.setOnClickListener {
            val intentBack = Intent(this, index::class.java)
            startActivity(intentBack)
            finish()
        }

        binding.textLogin2.setOnClickListener {
            val intentSignUp = Intent(this, LoginActivity::class.java)
            startActivity(intentSignUp)
            finish()
        }

        binding.signup.setOnClickListener {

            if (binding.Email.text.toString().isNotEmpty() && binding.Password.text.toString().isNotEmpty() && binding.RepeatPassword.text.toString().isNotEmpty()) {
                if (binding.Password.text.toString() == binding.RepeatPassword.text.toString()) {
                    firebaseAuth.createUserWithEmailAndPassword(binding.Email.text.toString(), binding.Password.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val userId = user?.uid

                                val hashMap = hashMapOf<String, Any>(
                                    "name" to binding.Nome.text.toString(),
                                    "surname" to binding.Cognome.text.toString(),
                                    "Email" to binding.Email.text.toString(),
                                    "tipoutente" to tipoutente
                                )

                                if (userId != null) {
                                    uploadData(hashMap, "users", userId, fireStoreDatabase)
                                }

                                Log.d("ID DELLO USER", userId.toString())
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    task.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
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

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            selezionaTipoUtente(isChecked)

        }

    }

    fun uploadData(hashMap:  HashMap<String, Any>, nomeTabella: String, id: String, fireStoreDatabase: FirebaseFirestore) {

        fireStoreDatabase.collection(nomeTabella)
            .document(id)
            .set(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "Added document with ID $id")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document $exception")
            }
    }


    private fun selezionaTipoUtente(isChecked: Boolean) {
        tipoutente= if (isChecked) "guidatore"
        else "consumatore"

    }




}

