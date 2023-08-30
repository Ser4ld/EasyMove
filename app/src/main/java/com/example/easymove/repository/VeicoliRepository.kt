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

    fun storeVehicleForUser(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via:String,
        CodicePostale: String,
        Capienza: String,
        TariffaKm: String,
        imageUri: Uri?,
        callback: (Boolean, String?) -> Unit
    ) {
        val storageRef = firebaseStorage.reference.child("images")
        val imageName = "$UserId."+imageUri?.lastPathSegment ?: "${System.currentTimeMillis()}.png"
        val imageRef = storageRef.child(imageName)

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Caricamento completato con successo
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    // Continua con il processo di creazione dell'annuncio e salva l'URL dell'immagine nel database
                    val veicolo = Veicolo(UserId, NomeVeicolo, Targa, Capienza, CittaMezzo, Via, CodicePostale, TariffaKm, imageUrl)
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

    suspend fun checkTargaExists(targa: String): Boolean {
        val snapshot = firestoreDatabase.collection("vans")
            .whereEqualTo("targa", targa)
            .get()
            .await()

        return !snapshot.isEmpty
    }

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



    fun getVeicoliListener(callback: (Boolean, String?, List<Veicolo>?) -> Unit): ListenerRegistration {
        return firestoreDatabase.collection("vans")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    callback(false, error.toString(), null)
                    return@addSnapshotListener
                }

                val veicoliList = mutableListOf<Veicolo>()
                for (document in snapshot?.documents ?: emptyList()) {
                    val van = document.toObject(Veicolo::class.java)
                    van?.let { veicoliList.add(it) }
                }

                callback(true, null, veicoliList)
            }
    }

}