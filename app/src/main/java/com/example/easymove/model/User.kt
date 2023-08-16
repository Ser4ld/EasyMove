package com.example.easymove.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class User(private val fireStoreDatabase: FirebaseFirestore) {

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
}
