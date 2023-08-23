package com.example.easymove.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.easymove.model.Veicolo
import com.google.firebase.storage.FirebaseStorage

private val firebaseAuth = FirebaseAuth.getInstance()
private val firestoreDatabase = FirebaseFirestore.getInstance()
private val firebaseStorage = FirebaseStorage.getInstance()

class VeicoliRepository {

    fun storeVehicleForUser(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via:String,
        NumeroCiv: String ,
        CodicePostale: String,
        Capienza: String,
        TariffaKm: String,
        imageUri: Uri?,
        callback: (Boolean, String?) -> Unit
    ) {
        val storageRef = firebaseStorage.reference.child("images")
        val imageName = "${System.currentTimeMillis()}.png"
        val imageRef = storageRef.child(imageName)

        imageRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Caricamento completato con successo
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    // Continua con il processo di creazione dell'annuncio e salva l'URL dell'immagine nel database
                    val veicolo = Veicolo(UserId, NomeVeicolo, Targa, Capienza, CittaMezzo, Via, CodicePostale, NumeroCiv, TariffaKm, imageUrl)
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



}