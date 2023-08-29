package com.example.easymove.repository
import com.example.easymove.model.Recensione
import com.example.easymove.model.Richiesta
import com.example.easymove.model.Veicolo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

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
        var richiesta = Richiesta("", guidatoreId, consumatoreId, targaveicolo, puntoPartenza, puntoArrivo, data, descrizione, statoRichiesta)
        uploadRichiesta(richiesta){ success, Errmsg ->
            if(success){
                callback(true, null)
            }else{
                callback(false, Errmsg)
            }
        }
    }

    fun uploadRichiesta(richiesta: Richiesta, callback: (Boolean, String?) -> Unit) {
        val newRichiesta = firestoreDatabase.collection("requests").document()
        richiesta.richiestaId = newRichiesta.id
        newRichiesta.set(richiesta)
            .addOnSuccessListener {
                callback(true, "Richiesta inoltrata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }

    fun getRichiesteListener(callback: (Boolean, String?, List<Richiesta>?) -> Unit): ListenerRegistration {
        return firestoreDatabase.collection("requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                val richiesteList = mutableListOf<Richiesta>()
                for (document in snapshot?.documents ?: emptyList()) {
                    val request = document.toObject(Richiesta::class.java)
                    request?.let { richiesteList.add(it) }
                }

                callback(true, null, richiesteList)
            }
    }



    fun updateRichiestaStato(richiestaId: String, nuovoStato: String, callback: (Boolean, String?) -> Unit) {
        val richiestaRef = firestoreDatabase.collection("requests").document(richiestaId)

        richiestaRef.update("stato", nuovoStato)
            .addOnSuccessListener {
                callback(true, "Stato della richiesta aggiornato correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore nell'aggiornamento dello stato della richiesta: ${exception.message}")
            }
    }




}
