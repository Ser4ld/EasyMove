package com.example.easymove.ViewModel

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.User
import com.example.easymove.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class UserViewModel: ViewModel() {
    private val userRepository = UserRepository()

    // Dichiarazione di 2 livedata che permettono di mantenere i dati dell'utente corrente e di tutti gli altri utenti
    val userDataLiveData: LiveData<User?> = userRepository.userDataLiveData
    val allUsersLiveData: LiveData<List<User>> = userRepository.allUsersLiveData

    // Funzione per recuperare i dati dell'utente corrente dal Firestore utilizzando UserRepository
    fun fetchUserData() {

        // Chiama il metodo di UserRepository per recuperare i dati dell'utente dal Firestore
        userRepository.fetchUserDataFromFirestore()
    }

    // Funzione per recuperare tutti gli utenti utilizzando UserRepository
    fun fetchAllUser(){
        // Chiama il metodo di UserRepository per recuperare tutti gli utenti
        userRepository.fetchAllUsers()
    }

    // Funzione per inviare un'email di reset della password
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        // Chiamata al UserRepository per gestire l'invio dell'email di reset della password
        userRepository.sendPasswordResetEmail(email, onSuccess, onFailure)
    }

    // Metodo per la modifica dell'indirizzo email dell'utente con riautenticazione.
    fun modifyMailWithReauthentication (current_mail: String, new_mail: String, password: String, callback: (Boolean, String?) -> Unit){

        // Ottenere l'istanza corrente dell'utente autenticato
        val user = FirebaseAuth.getInstance().currentUser

        // Verificare se l'utente è autenticato nel caso non lo sia
        // viene restituito un messaggio di errore tramite la callback
        if (user == null) {
            callback(false, "Utente non autenticato")
            return
        }

        // Richiamare il metodo del repository per la riautenticazione e l'aggiornamento dell'email
        userRepository.reauthenticateAndUpdateEmail(
            user,
            current_mail,
            new_mail,
            password
        ) { success, message ->
            if (success) {
                callback(true, "Indirizzo email aggiornato con successo")
            } else {
                callback(false, message)
            }
        }

    }

    // funzione che lega la view e la repository per il cambio dell'immagine di profilo
    fun updateImageUrl(userId: String, imageUrl: Uri, callback: (Boolean, String?) -> Unit) {

        // Utilizza l'oggetto userRepository per effettuare l'aggiornamento dell'immagine di profilo
        userRepository.updateImageUrl(userId, imageUrl) { success, errMsg ->

            // Gestisce l'esito dell'operazione
            if (success) {
                callback(true, "URL dell'immagine aggiornato con successo.")
            } else {
                callback(false, errMsg)
            }
        }
    }

    //Filtra una lista di utenti per trovare l'utente con l'ID specificato.
    fun FilterListById(userId: String, userList: List<User> ): User? {
        return userList.find { it.id == userId }
    }

    // verifica che l'utente sia un guidatore e restituisci true oppure se è
    // un consumatore e nel caso restituisci false
    fun checkUserType(userType: String): Boolean{
        if(userType == "guidatore"){
            return true
        }
        return false
    }
}