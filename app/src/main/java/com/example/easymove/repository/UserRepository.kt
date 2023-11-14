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


    // Funzione per creare un nuovo utente utilizzando Firebase Authentication e Firestore
    fun createUser(
        email: String,
        password: String,
        nome: String,
        cognome: String,
        tipoutente: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Utilizza Firebase Authentication per creare un nuovo utente con l'email e la password fornite
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Ottiene l'utente corrente dopo la creazione
                    val user = FirebaseAuth.getInstance().currentUser

                    // prendiamo l'id dell'utente corrente
                    val userId = user?.uid

                    // creazione dell'oggetto User con le informazioni dell'utente appena creato
                    val userObj = User(userId.orEmpty(), nome, cognome, email, tipoutente,"")

                    // Si esegue lo store su firestore
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

    // funzione per lo store dell'oggetto User su firestore
    private fun uploadUserData(user: User,callback: (Boolean, String) -> Unit) {

        //definiamo la collezione sulla quale memorizzeremo l'utente
        firestoreDatabase.collection("users")

            //definiamo l'id univoco del documento (viene utilizzato l'id dell'utente)
            .document(user.id)

            // Imposta i dati dell'utente nel documento
            .set(user)
            .addOnSuccessListener {
                callback(true, "registrazione effettuata")
            }
            .addOnFailureListener { exception ->
                callback(false, "Errore registrazione")
            }
    }

    //Aggiorna l'URL dell'immagine dell'utente corrente caricando un'immagine nel cloud storage e
    // aggiornando successivamente il documento utente nel database Firestore con l'URL risultante.
    fun updateImageUrl(userId: String, imageUri: Uri, callback: (Boolean, String?) -> Unit) {

        // Definiamo il percorso nel firebase cloud storage il nome dell'immagine di profilo dell'utente
        // è uguale al suo id
        val storageRef = firebaseStorage.reference.child("profile_images/$userId.png")

        // Carica l'immagine nel firebase cloud storage all'interno del percorso specificato
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            // Ottiene l'URL dell'immagine appena caricata
            storageRef.downloadUrl

        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                // Ottiene l'URL dell'immagine risultante dal cloud storage
                val imageUrl = task.result.toString()

                // Ottiene il riferimento al documento dell'utente corrente nel database Firestore
                val userRef = firestoreDatabase.collection("users").document(userId)

                // Aggiorna il campo "imageUrl" nel documento utente con il nuovo URL
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





    // Funzione per autenticare un utente utilizzando l'email e la password tramite FirebaseAuth
    fun authenticateUser(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Utilizza FirebaseAuth per eseguire l'autenticazione dell'utente
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Verifica se l'autenticazione è stata completata con successo se è così, si ottiene
                // l'utente corrente e viene richiamata la funzione fetchUserDataFromFirestore()
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser

                    // Funzione che permette di aggiornare il LiveData dell'utente
                    fetchUserDataFromFirestore()
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    // Funzione per inviare un'email di reset della password utilizzando Firebase Authentication
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {

        // tramite questa funzione specifica del firebaseauthentication viene mandata
        // la mail di reset password all'indirizzo specificato
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    // Funzione per ottenere l'utente corrente tramite il firebase auth
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    // Funzione per ottenere l'id dell'utente corrente
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // Funzione per verificare l'esistenza di un utente nel database Firestore
    // nel caso in cui questo esista restituisce il true nella funzione di callback
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
            }
            // Se la query non è stata eseguita con successo, gestisce l'eccezione
            else {
                val exception = task.exception
                if (exception != null) {
                    val errorMessage = exception.message ?: "Effettua il login"
                    callback(false, errorMessage)
                }

            }
        }
    }

//    fun getUserData(callback: (User?) -> Unit) {
//        val userId = getCurrentUserId()
//        if (userId != null) {
//            val docRef = firestoreDatabase.collection("users").document(userId)
//            docRef.get().addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val documentSnapshot = task.result
//                    if (documentSnapshot.exists()) {
//
//                        val userData = documentSnapshot.toObject(User::class.java)
//                        callback(userData)
//                    } else {
//                        callback(null)
//                    }
//                } else {
//                    callback(null)
//                }
//            }
//        } else {
//            callback(null)
//        }
//    }


    // Funzione per recuperare i dati dell'utente corrente dal Firestore
    fun fetchUserDataFromFirestore() {
        val userId = getCurrentUserId()

        // verifica che lo userid non sia nullo
        if (userId != null) {
            // cerca all'interno della collezione users il documento con id pari allo userid
            val docRef = firestoreDatabase.collection("users").document(userId)
            docRef.addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    // Gestisci l'errore
                    return@addSnapshotListener
                }

                // verifica che il documento restituito dal database in risposta
                // al confronto tra Id sia esistente e non nullo
                if (documentSnapshot != null && documentSnapshot.exists()) {

                    // Ottieni i dati dell'utente dal documento e convertili in un oggetto User
                    val userData = documentSnapshot.toObject(User::class.java)

                    // Aggiorna il LiveData con i nuovi dati dell'utente
                    _userDataLiveData.postValue(userData)
                } else {
                    // Se il documento non esiste, imposta il LiveData a null
                    _userDataLiveData.postValue(null)
                }
            }
        }
    }

    // Funzione per recuperare tutti gli utenti dal Firestore
    fun fetchAllUsers() {

        // Utilizza Firestore per ottenere la collezione di tutti gli utenti
        firestoreDatabase.collection("users")
            .get()
            .addOnCompleteListener { task ->

                // Verifica se la richiesta è stata completata con successo
                if (task.isSuccessful) {

                    // Inizializza una lista mutable di utenti
                    val userList = mutableListOf<User>()

                    // viene iterato il documento restituito da firebase e
                    // trasforma ogniuno di essi in un oggetto User
                    for (document in task.result!!) {
                        val userData = document.toObject(User::class.java)
                        userList.add(userData)
                    }

                    // Aggiorna il LiveData con la lista di tutti gli utenti
                    _allUsersLiveData.postValue(userList)
                } else {
                    // Se la richiesta fallisce, imposta il LiveData a null
                    _allUsersLiveData.postValue(null)
                }
            }
    }

//
//    fun getDataFromFirestore(): Task<DocumentSnapshot>? {
//        val docRef = getCurrentUserId()?.let { firestoreDatabase.collection("users").document(it) }
//        return docRef?.get()
//    }


    // Riautentica l'utente e aggiorna l'indirizzo email.
    fun reauthenticateAndUpdateEmail(
        user: FirebaseUser,
        currentMail: String,
        newMail: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Crea le credenziali per la riautenticazione
        val credential = EmailAuthProvider.getCredential(currentMail, password)

        // Esegue la riautenticazione e gestisce le eccezioni legate ad essa
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {

                // Se la riautenticazione ha successo, aggiorna l'indirizzo email dell'utente nel firebase Auth
                user.updateEmail(newMail).addOnCompleteListener { updateEmailTask ->
                    if (updateEmailTask.isSuccessful) {

                        // Se la riautenticazione e l'aggiornamento dell'indirizzo email nel firebase Auth hanno successo
                        // viene richiamato il metodo updateUserDocument che aggiorna la mail nel documento del firestore
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

    // Aggiorna l'indirizzo email nel documento utente in Firestore.
    private fun updateUserDocument(userId: String, newEmail: String, callback: (Boolean, String?) -> Unit) {

        // Ottieni il riferimento al documento utente passando lo userId
        val userDocRef = firestoreDatabase.collection("users").document(userId)

        // Aggiorna l'indirizzo email nel documento utente nel firestore database tramite il metodo update
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
