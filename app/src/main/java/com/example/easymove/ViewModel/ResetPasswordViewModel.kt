package com.example.easymove.viewmodel

import com.example.easymove.repository.UserRepository

class ResetPasswordViewModel(private val userRepository: UserRepository) {

    fun modifyPassword(email: String, callback: (Boolean, String?) -> Unit) {
        if (email.isNotEmpty()) {
            userRepository.sendModifyPassword(email) { success, message ->
                if (success) {
                    callback(true, null)
                } else {
                    callback(false, message)
                }
            }
        } else {
            callback(false, "Email non inserita")
        }
    }

}