package com.example.easymove.profilo

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser


    fun getUserId(): String {
        val userId = user?.uid
        return userId.toString()
    }

    fun getDataFromFirestore(): Task<DocumentSnapshot> {

        val docRef = db.collection("users").document(getUserId())
        return docRef.get()

    }

    fun updateMail(key: String, newValue: String) {
        val docRef = db.collection("users").document(getUserId())

        docRef.update(key , newValue)
            .addOnSuccessListener {
                Log.d("Email Modificata", "successo")
            }
            .addOnFailureListener { e ->
                Log.d("Email Modificata", "fallito")
            }


    }




}