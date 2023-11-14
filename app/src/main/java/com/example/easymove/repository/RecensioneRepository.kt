package com.example.easymove.repository

import com.example.easymove.model.Recensione
import com.example.easymove.model.Veicolo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecensioneRepository {

    private val firestoreDatabase = FirebaseFirestore.getInstance()


    // Registra una nuova recensione nel database.
    fun storeRecensione(
        richiestaId: String,
        consumatoreId: String,
        guidatoreId: String,
        valutazione: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {

        // Crea un oggetto Recensione con i dati forniti
        val recensione = Recensione(richiestaId,"", consumatoreId, guidatoreId, valutazione, descrizione)

        // Carica la recensione nel database tramite il metodo uploadRecensione
        uploadRecensione(recensione) { success, errMsg ->
            if (success) {
                callback(true, null)
            } else {
                callback(false, errMsg)
            }
        }
    }

    // Carica una recensione nel database Firestore.
    fun uploadRecensione(recensione: Recensione, callback: (Boolean, String?) -> Unit) {

        // Ottieni il documento delle recensioni dal database
        val newRecensione = firestoreDatabase.collection("reviews").document()

        // Setta l'id della recensione visualizzata nel database
        recensione.recensioneId = newRecensione.id

        // Salva l'oggetto Recensione nel documento nel database
        newRecensione.set(recensione)
            .addOnSuccessListener {
                callback(true, "Recensione inviata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }

    // Ottieni un listener per gli aggiornamenti sulla lista di recensioni dal Firestore
    fun getRecensioniListener(callback: (Boolean, String?, List<Recensione>?) -> Unit): ListenerRegistration {

        // Viene ritornata la collezione di recensioni
        return firestoreDatabase.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                // Inizializza una lista mutable per le recensioni
                val recensioneList = mutableListOf<Recensione>()

                // Itera attraverso i documenti restituiti e crea oggetti Recensione
                for (document in snapshot?.documents ?: emptyList()) {
                    val review = document.toObject(Recensione::class.java)
                    review?.let { recensioneList.add(it) }
                }

                // Esegui il callback con la lista di recensioni aggiornata
                callback(true, null, recensioneList)
            }
    }


}