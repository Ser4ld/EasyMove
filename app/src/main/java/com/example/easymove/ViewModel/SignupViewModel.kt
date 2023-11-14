package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.model.User
import com.example.easymove.repository.UserRepository


class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Funzione per gestire il processo di registrazione di un nuovo utente, verifica prima che tutti i campi siano
    // non vuoti per poi andare a fare un controllo sulla stesura della password ed in fine va a controllare se la
    // password risulta uguale a repeatpassword se tutti questi controlli vano a buon fine si richiama il metodo create user
    // in caso contrario si gestisce l'errore tramite callback
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

                    // Utilizza il UserRepository per creare un nuovo utente
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


    // Funzione per verificare se una password soddisfa i requisiti minimi:
    // - Almeno una lettera minuscola
    // - Almeno una lettera maiuscola
    // - Almeno un numero
    // - Almeno un carattere speciale tra !@#$%^&*()-_=+{};:,<.>/?
    // - Lunghezza minima di 8 caratteri
    fun checkPassword(password: String): Boolean {

        // Utilizza una regex per eseguire la verifica dei requisiti della password
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()\\-_=+{};:,<.>/?]).{8,}\$")

        // Restituisce true se la password soddisfa la regex, altrimenti false
        return regex.matches(password)

    }

}