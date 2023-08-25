package com.example.easymove.repository

import com.example.easymove.enum.StatoRichiesta
import com.example.easymove.model.Richiesta
import com.example.easymove.model.Veicolo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val firebaseAuth = FirebaseAuth.getInstance()
private val firestoreDatabase = FirebaseFirestore.getInstance()

class RichiestaRepository {

    /*fun storeRequest(
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        data: String,
        descrizione: String,
        statoRichiesta: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        val requestData = hashMapOf(
            "guidatoreId" to guidatoreId,
            "consumatoreId" to consumatoreId,
            "targaveicolo" to targaveicolo,
            "puntoPartenza" to puntoPartenza,
            "puntoArrivo" to puntoArrivo,
            "data" to data,
            "descrizione" to descrizione,
            "statoRichiesta" to statoRichiesta
        )

        firestoreDatabase.collection("requests")
            .add(requestData)
            .addOnSuccessListener {
                callback(true, null) // Successo, nessun errore
            }
            .addOnFailureListener {
                callback(false, "Errore invio richiesta") // Errore con messaggio
            }
    }*/

    fun storeRequest(
        richiestaId: String,
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        data: String,
        descrizione: String,
        statoRichiesta: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit)
    {
        val richiesta = Richiesta(richiestaId, guidatoreId, consumatoreId, targaveicolo, puntoPartenza, puntoArrivo, data, descrizione, statoRichiesta)
        uploadRichiesta(richiesta,richiestaId){ success, Errmsg ->
            if(success){
                callback(true, null)
            }else{
                callback(false, Errmsg)
            }
        }
    }

    fun uploadRichiesta(richiesta: Richiesta, richiestaId : String, callback: (Boolean, String?) -> Unit) {
        firestoreDatabase.collection("requests")
            .document(richiestaId)
            .set(richiesta)
            .addOnSuccessListener {
                callback(true, "Richiesta inoltrata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }

}
