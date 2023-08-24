package com.example.easymove.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val firebaseAuth = FirebaseAuth.getInstance()
private val firestoreDatabase = FirebaseFirestore.getInstance()

class RichiestaRepository {

    fun storeRequest(
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        data: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        val requestData = hashMapOf(
            "guidatoreId" to guidatoreId,
            "consumatoreId" to consumatoreId,
            "targaveicolo" to targaveicolo,
            "puntoPartenza" to puntoPartenza,
            "puntoArrivo" to puntoArrivo,
            "data" to data,
            "descrizione" to descrizione
        )

        firestoreDatabase.collection("requests")
            .add(requestData)
            .addOnSuccessListener {
                callback(true, null) // Successo, nessun errore
            }
            .addOnFailureListener {
                callback(false, "Errore invio richiesta") // Errore con messaggio
            }
    }

}
