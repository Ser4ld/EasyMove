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

    val userDataLiveData: LiveData<User?> = userRepository.userDataLiveData
    val allUsersLiveData: LiveData<List<User>> = userRepository.allUsersLiveData

    fun fetchUserData() {
        userRepository.fetchUserDataFromFirestore()
    }

    fun fetchAllUser(){
        userRepository.fetchAllUsers()
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        userRepository.sendPasswordResetEmail(email, onSuccess, onFailure)
    }

    fun modifyMailWithReauthentication (current_mail: String, new_mail: String, password: String, callback: (Boolean, String?) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            callback(false, "Utente non autenticato")
            return
        }

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

    fun updateImageUrl(userId: String, imageUrl: Uri, callback: (Boolean, String?) -> Unit) {
        userRepository.updateImageUrl(userId, imageUrl) { success, errMsg ->
            if (success) {
                callback(true, "URL dell'immagine aggiornato con successo.")
            } else {
                callback(false, errMsg)
            }
        }
    }

    fun FilterListById(userId: String, userList: List<User> ): User? {
        return userList.find { it.id == userId }
    }

    fun checkUserType(userType: String): Boolean{
        if(userType == "guidatore"){
            return true
        }
        return false
    }
}