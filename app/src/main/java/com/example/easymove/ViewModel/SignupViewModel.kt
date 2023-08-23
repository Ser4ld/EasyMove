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
            if(checkPassword(password)) {
                if (password == repeatPassword) {
                    userRepository.createUser(email, password, nome, cognome, tipoutente, callback)
                } else {
                    callback(false, "Le Password non sono uguali")
                }
            }else{
                callback(false, "la password deve contenere almeno una lettera maiuscola, un numero e un carattere speciale e deve essere lunga minimo 8 caratteri")
            }
        } else {
            callback(false, "Username o Password non inseriti")
        }
    }


    fun checkPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        return regex.matches(password)
    }

    // Altri metodi del ViewModel
}