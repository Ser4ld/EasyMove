package com.example.easymove.ViewModel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.example.easymove.databinding.HomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun fetchAndSetTipoutente(userId: String?, callback: (Boolean) -> Unit) {
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val tipoutente = documentSnapshot.getString("tipoutente")
                        val isGuidatore = tipoutente == "guidatore"
                        callback(isGuidatore)
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Errore durante la lettura dal Firestore", exception)
                    callback(false)
                }
        }
    }
}
