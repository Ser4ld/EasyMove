package com.example.easymove.ViewModel

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.easymove.home.HomeActivity
import com.example.easymove.login.LoginActivity
import com.example.easymove.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel(private val activity: AppCompatActivity, private val userModel: User) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreDatabase = FirebaseFirestore.getInstance()
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            userModel.authenticateUser(email, password) { success, message ->
                if (success) {
                    callback(true, null)
                } else {
                    callback(false, message)
                }
            }

        } else {
            callback(false, "Username o Password non inseriti")
        }

    }

    fun autoLogin() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = firestoreDatabase.collection("users").document(userId)
            //controllo se l'utente esiste nel database per gestire la casistica in cui viene eliminato o disabilitato
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result

                    if (document != null && document.exists()) {
                        // L'utente esiste nel database, esegui l'autologin
                        val intentAutoLogin = Intent(activity, HomeActivity::class.java)
                        activity.startActivity(intentAutoLogin)
                        activity.finish()
                    } else {

                    }
                } else {
                    // Gestione dell'errore nel caso in cui non sia possibile accedere al documento
                    val exception = task.exception
                    if (exception != null) {
                        val errorMessage = exception.message ?: "Errore sconosciuto"
                        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

}