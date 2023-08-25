package com.example.easymove.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.easymove.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

private val firebaseAuth = FirebaseAuth.getInstance()
private val firestoreDatabase = FirebaseFirestore.getInstance()
private val firebaseStorage= FirebaseStorage.getInstance()
class UserRepository() {
    private val _userDataLiveData = MutableLiveData<User?>()
    private val _allUsersLiveData = MutableLiveData<List<User>>()
    val userDataLiveData: LiveData<User?> = _userDataLiveData
    val allUsersLiveData: LiveData<List<User>> = _allUsersLiveData

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

                    val userObj = User(userId.orEmpty(), nome, cognome, email, tipoutente,"")
                    uploadUserData(userObj){ success, Errmsg ->
                        if(success){
                            callback(true, null)
                        }else{
                            callback(false, Errmsg)
                        }

                    }


                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    private fun uploadUserData(user: User,callback: (Boolean, String) -> Unit) {
        firestoreDatabase.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                callback(true, "registrazione effettuata")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore registrazione")
            }
    }

    fun updateImageUrl(userId: String, imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val storageRef = firebaseStorage.reference.child("profile_images/$userId.jpg")

        val uploadTask = storageRef.putFile(imageUri)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val imageUrl = task.result.toString()

                val userRef = firestoreDatabase.collection("users").document(userId)

                userRef.update("imageUrl", imageUrl)
                    .addOnSuccessListener {
                        callback(true, "Immagine caricata con successo.")
                    }
                    .addOnFailureListener { exception ->
                        callback(false, "Errore durante il caricamento dell'immagine.")
                    }
            } else {
                callback(false, "Errore durante il caricamento dell'immagine.")
            }
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

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
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

    fun fetchAllUsers() {
        firestoreDatabase.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userList = mutableListOf<User>()

                    for (document in task.result!!) {
                        val userData = document.toObject(User::class.java)
                        userList.add(userData)
                    }

                    _allUsersLiveData.postValue(userList)
                } else {
                    _allUsersLiveData.postValue(null)
                }
            }
    }


    fun getDataFromFirestore(): Task<DocumentSnapshot>? {
        val docRef = getCurrentUserId()?.let { firestoreDatabase.collection("users").document(it) }
        return docRef?.get()
    }


    /*Modifica email*/
    fun reauthenticateAndUpdateEmail(
        user: FirebaseUser,
        currentMail: String,
        newMail: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val credential = EmailAuthProvider.getCredential(currentMail, password)

        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                user.updateEmail(newMail).addOnCompleteListener { updateEmailTask ->
                    if (updateEmailTask.isSuccessful) {
                        updateUserDocument(user.uid, newMail) { updateSuccess, updateMessage ->
                            if (updateSuccess) {
                                callback(true, "Indirizzo email aggiornato con successo")
                            } else {
                                callback(false, updateMessage)
                            }
                        }
                    } else {
                        callback(false, "Errore durante l'aggiornamento dell'indirizzo email")
                    }
                }
            } else {
                callback(false, "Errore durante la riautenticazione")
            }
        }
    }

    private fun updateUserDocument(userId: String, newEmail: String, callback: (Boolean, String?) -> Unit) {

        val userDocRef = firestoreDatabase.collection("users").document(userId)

        userDocRef.update("email", newEmail)
            .addOnCompleteListener { updateTask ->
                if (updateTask.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, "Errore durante l'aggiornamento dell'indirizzo email nel documento")
                }
            }
    }

}
