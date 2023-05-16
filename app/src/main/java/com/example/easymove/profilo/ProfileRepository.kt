package com.example.easymove.profilo

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ProfileRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private val docRef= db.collection("users").document(getUserId())




    fun getUserId(): String {
        val userId = user?.uid
        return userId.toString()
    }

    fun getUserEmail(): Task<String> {

        return docRef.get().continueWith { task ->
            val documentSnapshot = task.result
            if (documentSnapshot.exists()) {
                val email = documentSnapshot.getString("Email").toString()
                email // Restituisce direttamente il valore della mail
            } else {
                Log.e("documento", "id documento non esistente")
                "" // Restituisce una stringa vuota nel caso il documento non esista
            }
        }
    }

    fun getUserName(): Task<String> {

        return docRef.get().continueWith { task ->
            val documentSnapshot = task.result
            if (documentSnapshot.exists()) {
                val email = documentSnapshot.getString("name").toString()
                email // Restituisce direttamente il valore della mail
            } else {
                Log.e("documento", "id documento non esistente")
                "" // Restituisce una stringa vuota nel caso il documento non esista
            }
        }
    }

    fun getUserSurname(): Task<String> {

        return docRef.get().continueWith { task ->
            val documentSnapshot = task.result
            if (documentSnapshot.exists()) {
                val email = documentSnapshot.getString("surname").toString()
                email // Restituisce direttamente il valore della mail
            } else {
                Log.e("documento", "id documento non esistente")
                "" // Restituisce una stringa vuota nel caso il documento non esista
            }
        }
    }




}