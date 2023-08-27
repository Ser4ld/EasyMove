package com.example.easymove.repository

import com.example.easymove.model.Recensione
import com.google.firebase.firestore.FirebaseFirestore

class RecensioneRepository {

    private val firestoreDatabase = FirebaseFirestore.getInstance()


    fun storeRecensione(
        stelline: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        val recensione = Recensione("",stelline, descrizione)
        uploadRecensione(recensione) { success, errMsg ->
            if (success) {
                callback(true, null)
            } else {
                callback(false, errMsg)
            }
        }
    }

    fun uploadRecensione(recensione: Recensione, callback: (Boolean, String?) -> Unit) {
        val newRecensione = firestoreDatabase.collection("reviews").document()
        recensione.id = newRecensione.id
        newRecensione.set(recensione)
            .addOnSuccessListener {
                callback(true, "Recensione inviata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }


}