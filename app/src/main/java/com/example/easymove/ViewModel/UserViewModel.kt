package com.example.easymove.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.User
import com.example.easymove.repository.UserRepository

class UserViewModel: ViewModel() {
    private val userRepository = UserRepository()

    val userDataLiveData: LiveData<User?> = userRepository.userDataLiveData

    fun fetchUserData() {
        userRepository.fetchUserDataFromFirestore()
    }


}