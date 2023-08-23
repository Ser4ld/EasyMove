package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.repository.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            userRepository.authenticateUser(email, password) { success, message ->
                if (success) {
                    callback(true, null)
                } else {
                    callback(false, message)
                }
            }
        } else {
            callback(false, "Username o Password non inseriti")
        }
    }



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