package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Funzione per gestire il processo di login dell'utente
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {

        // Verifica che l'email e la password non siano vuote
        if (email.isNotEmpty() && password.isNotEmpty()) {

            // Autentica l'utente tramite il UserRepository
            userRepository.authenticateUser(email, password) { success, message ->

                // se la funzione ha successo restituisci un esito positivo nella callback
                if (success) {
                    callback(true, null)
                }

                // se la funzione non ha successo vengono gestite le eccezioni tramite messaggi che
                // verranno visualizzati come toast
                else {
                    callback(false, message)
                }
            }
        } else {
            callback(false, "Username o Password non inseriti")
        }
    }


    // Funzione per tentare l'autenticazione automatica dell'utente
    // si ottine prima l'istanza dell'utente corrente e se questa non è nulla
    // si va a verificare che l'utente sia memorizzato nel database tramite la checkUser()
    fun autologin(callback: (Boolean, String?) -> Unit) {

        val currentUser = userRepository.getCurrentUser()

        if (currentUser != null) {
            val userId = currentUser.uid
            userRepository.checkUser(userId, "users") { userExists, errorMessage ->
                if (userExists) {
                    callback(true, "Autologin effettuato")
                } else {
                    callback(false, "Errore sconosciuto")
                }
            }
        } else {
            callback(false, "Utente non autenticato")
        }
    }
}