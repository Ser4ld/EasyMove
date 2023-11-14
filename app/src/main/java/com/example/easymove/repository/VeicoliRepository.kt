package com.example.easymove.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easymove.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.easymove.model.Veicolo
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage

private val firestoreDatabase = FirebaseFirestore.getInstance()
private val firebaseStorage = FirebaseStorage.getInstance()

class VeicoliRepository {

    // Registra un veicolo associato all'utente guidatore, caricando un'immagine associata al veicolo
    fun storeVehicleForUser(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via:String,
        CodicePostale: String,
        latitude:String,
        longitude:String,
        Capienza: String,
        TariffaKm: String,
        imageUri: Uri?,
        callback: (Boolean, String?) -> Unit
    ) {
        // Ottiene il riferimento al percorso di storage per le immagini
        val storageRef = firebaseStorage.reference.child("images")

        // Genera un nome univoco per l'immagine
        val imageName = "$UserId."+imageUri?.lastPathSegment ?: "${System.currentTimeMillis()}.png"

        // Ottiene il riferimento specifico per l'immagine
        val imageRef = storageRef.child(imageName)

        // Carica l'immagine nel percorso di storage
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Caricamento completato con successo
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    // Crea un oggetto Veicolo con i dati forniti
                    val veicolo = Veicolo(UserId, NomeVeicolo, Targa, Capienza, CittaMezzo, Via, CodicePostale,latitude, longitude, TariffaKm, imageUrl)

                    // Carica il veicolo nel database tramite il metodo uploadVeicolo
                    uploadVeicolo(veicolo){success, Msg ->
                        if(success){
                            callback(true, Msg)
                        }else{
                            callback(false, Msg)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Errore durante il caricamento dell'immagine
                callback(false, "${exception.message}")
            }
    }

    // Funzione che entra nel firestore database e controlla se all'interno della collezione vans
    // ci sia un altro veicolo registrato con la targa passata come parametro
    suspend fun checkTargaExists(targa: String): Boolean {
        val snapshot = firestoreDatabase.collection("vans")
            .whereEqualTo("targa", targa)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    // Carica un veicolo nel database Firestore
    fun uploadVeicolo(veicolo : Veicolo, callback: (Boolean, String?) -> Unit){
        firestoreDatabase.collection("vans")
            .document(veicolo.targa)
            .set(veicolo)
            .addOnSuccessListener {
                callback(true, "registrazione veicolo effettuata")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore registrazione veicolo")
            }
    }




    // Ottieni un listener per gli aggiornamenti sulla lista di veicoli dal Firestore
    fun getVeicoliListener(callback: (Boolean, String?, List<Veicolo>?) -> Unit): ListenerRegistration {
        // viene ritornata la collezione di vans
        return firestoreDatabase.collection("vans")
            .addSnapshotListener { snapshot, error ->
                // viene controllato se il return è andato a buon fine, nel caso non lo fosse
                // restituisce una callback contenente un messaggio di errore
                if (error != null) {
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                // Inizializza una lista mutable per i veicoli
                val veicoliList = mutableListOf<Veicolo>()

                // Itera attraverso i documenti restituiti relativi alla collezione vans e crea oggetti Veicolo
                for (document in snapshot?.documents ?: emptyList()) {
                    val van = document.toObject(Veicolo::class.java)
                    van?.let { veicoliList.add(it) }
                }

                // Esegui il callback con la lista di veicoli aggiornata
                callback(true, null, veicoliList)
            }
    }

    // Funzione che permette di eliminare il documento del veicolo dal firestore database
    fun deleteVeicolo(veicoloId: String, callback: (Boolean, String?) -> Unit) {
        val veicoloRef = firestoreDatabase.collection("vans").document(veicoloId)

        veicoloRef.delete()
            .addOnSuccessListener {
                callback(true, "Veicolo eliminato correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore durante l'eliminazione del veicolo: ${exception.message}")
            }
    }



    // Aggiorna le informazioni del veicolo nel database Firestore.
    fun updateVeicoloFirestore(veicolo: Veicolo, callback: (Boolean, String?) -> Unit){
        val veicoloRef = firestoreDatabase.collection("vans").document(veicolo.targa)

        // Imposta le nuove informazioni del veicolo nel documento Firestore
        veicoloRef.set(veicolo)

            .addOnSuccessListener {
                callback(true, "Veicolo aggiornato correttamente")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore durante l'aggiornamento del veicolo: ${exception.message}")
            }
    }

    //Aggiorna le informazioni del veicolo, inclusa l'immagine, nel database Firebase.
    fun updateVeicolo(veicolo: Veicolo,imageUri: Uri?,callback: (Boolean, String?) -> Unit) {

        // Verifica se è stata fornita un'immagine per l'aggiornamento
        if(imageUri!= null){
            val storageRef = firebaseStorage.reference.child("images")
            val imageName = "${veicolo.id}."+imageUri?.lastPathSegment ?: "${System.currentTimeMillis()}.png"
            val imageRef = storageRef.child(imageName)

            // Carica l'immagine nel cloud storage di Firebase
            imageRef.putFile(imageUri!!)

                .addOnSuccessListener { taskSnapshot ->
                    // Caricamento completato con successo
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        veicolo.imageUrl= imageUrl
                        // Continua con l'aggiornamento nel database Firestore
                        updateVeicoloFirestore(veicolo){success, message ->
                            if(success){
                                callback(true, message)
                            }else{
                                callback(false, message)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Errore durante il caricamento dell'immagine
                    callback(false, "${exception.message}")
                }
        }else{
            // Se non è stata fornita un'immagine, esegui solo l'aggiornamento nel database Firestore
            updateVeicoloFirestore(veicolo){success, message ->
                if(success){
                    callback(true, message)
                }else{
                    callback(false, message)
                }
            }
        }

    }

}