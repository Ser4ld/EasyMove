package com.example.easymove.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easymove.model.User
import com.example.easymove.profilo.db
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

private val firebaseAuth = FirebaseAuth.getInstance()
private val firestoreDatabase = FirebaseFirestore.getInstance()

class UserRepository() {
    private val _userDataLiveData = MutableLiveData<User?>()
    val userDataLiveData: LiveData<User?> = _userDataLiveData

    fun createUser(
        email: String,
        password: String,
        nome: String,
        cognome: String,
        tipoutente: String,
        callback: (Boolean, String?) -> Unit
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userId = user?.uid

                    val userObj = User(userId.orEmpty(), nome, cognome, email, tipoutente)
                    uploadUserData(userObj)

                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    private fun uploadUserData(user: User) {
        firestoreDatabase.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                // Logica in caso di successo
            }
            .addOnFailureListener { exception ->
                // Logica in caso di fallimento
            }
    }

    /*autenticazione utente */
    fun authenticateUser(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    // Qui puoi ottenere informazioni sull'utente loggato, se necessario
                    fetchUserDataFromFirestore()
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun sendModifyPassword(email:String, callback: (Boolean, String?) -> Unit){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, null)
            }
            else{
                callback(false, task.exception?.message)
            }
        }
    }


    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun checkUser (id: String, collection: String, callback: (Boolean, String?) -> Unit) {
        val userRef = firestoreDatabase.collection(collection).document(id)

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
            val document = task.result
            if (document != null && document.exists()){
                    callback(true,null)
                }else {
                    callback(false, "il documento non esiste")
                }
            }else {
                val exception = task.exception
                if (exception != null) {
                    val errorMessage = exception.message ?: "Effettua il login"
                    callback(false, errorMessage)
                }

            }
        }
    }

    fun getUserData(callback: (User?) -> Unit) {
        val userId = getCurrentUserId()
        if (userId != null) {
            val docRef = firestoreDatabase.collection("users").document(userId)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot.exists()) {

                        val userData = documentSnapshot.toObject(User::class.java)
                        callback(userData)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }

    fun fetchUserDataFromFirestore() {
        val userId = getCurrentUserId()

        if (userId != null) {
            val docRef = firestoreDatabase.collection("users").document(userId)
            docRef.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    // Gestisci l'errore
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(User::class.java)
                    _userDataLiveData.postValue(userData)
                } else {
                    _userDataLiveData.postValue(null)
                }
            }
        }
    }


    fun getDataFromFirestore(): Task<DocumentSnapshot>? {
        val docRef = getCurrentUserId()?.let { db.collection("users").document(it) }
        return docRef?.get()
    }

}
