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

    //Memorizza una nuova richiesta nel firestore database tramite il metodo uploadRichiesta
    fun storeRequest(
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        data: String,
        descrizione: String,
        statoRichiesta: String,
        prezzo: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit)
    {
        //viene creato un oggetto richiesta
        var richiesta = Richiesta("", guidatoreId, consumatoreId, targaveicolo, puntoPartenza, puntoArrivo, data, descrizione, statoRichiesta,prezzo)

        // l'oggetto richiesta viene passato come parametro alla funzione uploadRichiesta
        // che memorizza la richiesta nel database
        uploadRichiesta(richiesta){ success, Errmsg ->
            if(success){
                callback(true, null)
            }else{
                callback(false, Errmsg)
            }
        }
    }


    // Carica una nuova richiesta nel database Firestore
    fun uploadRichiesta(richiesta: Richiesta, callback: (Boolean, String?) -> Unit) {

        // Ottiene un nuovo documento nel percorso "requests" nel database Firestore
        val newRichiesta = firestoreDatabase.collection("requests").document()

        // Assegna l'ID  al campo richiestaId dell'oggetto Richiesta
        richiesta.richiestaId = newRichiesta.id

        // Salva l'oggetto Richiesta nel documento appena creato nel database
        newRichiesta.set(richiesta)
            .addOnSuccessListener {
                callback(true, "Richiesta inoltrata correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore: ${exception.message}")
            }
    }

    // Ottieni un listener per gli aggiornamenti sulla lista di richieste dal Firestore
    fun getRichiesteListener(callback: (Boolean, String?, List<Richiesta>?) -> Unit): ListenerRegistration {
        // restituisce la collezione di richieste
        return firestoreDatabase.collection("requests")
            .addSnapshotListener { snapshot, error ->
                // Verifica se si è verificato un errore durante il recupero dei dati
                if (error != null) {
                    // Se si è verificato un errore, esegui il callback con informazioni sull'errore
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                // Inizializza una lista mutable per le richieste
                val richiesteList = mutableListOf<Richiesta>()

                // Itera attraverso i documenti restituiti e crea oggetti Richiesta
                for (document in snapshot?.documents ?: emptyList()) {
                    val request = document.toObject(Richiesta::class.java)
                    request?.let { richiesteList.add(it) }
                }

                // Esegui il callback con la lista di richieste aggiornata
                callback(true, null, richiesteList)
            }
    }



    // Aggiorna lo stato della richiesta nel firestore database
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
