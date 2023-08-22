package com.example.easymove.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.User
import com.example.easymove.profilo.MessageListener
import com.example.easymove.profilo.db
import com.example.easymove.repository.UserRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(private val userRepository: UserRepository): ViewModel() {




}