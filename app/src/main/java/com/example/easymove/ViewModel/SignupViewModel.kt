package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.model.User
import com.example.easymove.repository.UserRepository


class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun signUp(
        email: String,
        password: String,
        repeatPassword: String,
        nome: String,
        cognome: String,
        tipoutente: String,
        callback: (Boolean, String?) -> Unit
    ) {
        if (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty() && nome.isNotEmpty() && cognome.isNotEmpty()) {
            if (password == repeatPassword) {
                userRepository.createUser(email, password, nome, cognome, tipoutente, callback)
            } else {
                callback(false, "Le Password non sono uguali")
            }
        } else {
            callback(false, "Username o Password non inseriti")
        }
    }

    // Altri metodi del ViewModel
}