package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.model.User

class LoginViewModel(private val userModel: User) : ViewModel() {

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            userModel.authenticateUser(email, password) { success, message ->
                if (success) {
                    callback(true, null)
                } else {
                    callback(false, message)
                }
            }

        }
        else {
            callback(false, "Username o Password non inseriti")
        }

    }

}