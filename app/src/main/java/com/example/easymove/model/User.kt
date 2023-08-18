package com.example.easymove.model

import android.content.Intent
import com.example.easymove.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


private val user = FirebaseAuth.getInstance().currentUser

class User(private val fireStoreDatabase: FirebaseFirestore) {


    fun getUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun createUser(
        email: String,
        password: String,
        nome: String,
        cognome: String,
        tipoutente: String,
        callback: (Boolean, String?) -> Unit
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid

                    val hashMap = hashMapOf<String, Any>(
                        "name" to nome,
                        "surname" to cognome,
                        "Email" to email,
                        "tipoutente" to tipoutente
                    )

                    if (userId != null) {
                        uploadData(hashMap, "users", userId)
                    }

                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun uploadData(hashMap: HashMap<String, Any>, nomeTabella: String, id: String) {
        fireStoreDatabase.collection(nomeTabella)
            .document(id)
            .set(hashMap)
            .addOnSuccessListener {
                // Logica in caso di successo
            }
            .addOnFailureListener { exception ->
                // Logica in caso di fallimento
            }
    }

    /*autenticazione utente */
    fun authenticateUser(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    // Qui puoi ottenere informazioni sull'utente loggato, se necessario
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }




}

