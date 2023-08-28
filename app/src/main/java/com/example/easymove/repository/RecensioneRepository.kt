package com.example.easymove.repository

import com.example.easymove.model.Recensione
import com.example.easymove.model.Veicolo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecensioneRepository {

    private val firestoreDatabase = FirebaseFirestore.getInstance()


    fun storeRecensione(
        idCreatore: String,
        idRicevitore: String,
        stelline: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        val recensione = Recensione("", idCreatore, idRicevitore, stelline, descrizione)
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
        recensione.idRecensione = newRecensione.id
        newRecensione.set(recensione)
            .addOnSuccessListener {
                callback(true, "Recensione inviata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }

    fun getRecensioniListener(callback: (Boolean, String?, List<Recensione>?) -> Unit): ListenerRegistration {
        return firestoreDatabase.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                val recensioneList = mutableListOf<Recensione>()
                for (document in snapshot?.documents ?: emptyList()) {
                    val review = document.toObject(Recensione::class.java)
                    review?.let { recensioneList.add(it) }
                }

                callback(true, null, recensioneList)
            }
    }


}