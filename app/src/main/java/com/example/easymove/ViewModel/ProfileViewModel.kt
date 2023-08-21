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
/*tutti i dati presi dalla repository --> creo un oggetto profileRepository*/




    /*private val data: MutableLiveData<String?> = MutableLiveData()*/
    private val data: HashMap<String, MutableLiveData<String?>> = hashMapOf(
        "email" to MutableLiveData(),
        "name" to MutableLiveData(),
        "surname" to MutableLiveData(),
        "id" to MutableLiveData(),
        "tipoutente" to MutableLiveData()

    )


    fun fetchData() {

        userRepository.getDataFromFirestore()?.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val result: DocumentSnapshot? = task.result
                val email =
                    result?.getString("Email")
                val name = result?.getString("name")
                val surname = result?.getString("surname")

                data["email"]?.value = email
                data["name"]?.value = name
                data["surname"]?.value = surname
                data["id"]?.value= userRepository.getCurrentUserId()

            }
        })
    }

    fun getData(): HashMap<String, MutableLiveData<String?>> {
        return data
    }

    fun updateEmailWithReauthentication(key: String, newEmail: String, password: String, messageListener: MessageListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val email = user.email

            if (!isValidEmail(newEmail)) {
                // La nuova email non è correttamente formattata
                Log.e("User", "La nuova email non è correttamente formattata")
                messageListener.showMessage("La nuova email non è correttamente formattata")

                return
            }

            // Riautentica l'utente con la password attuale
            val credentials = EmailAuthProvider.getCredential(email!!, password)
            user.reauthenticate(credentials)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Verifica se la nuova email è già utilizzata da un altro utente nel sistema di autenticazione
                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(newEmail)
                            .addOnCompleteListener { signInMethodsTask ->
                                if (signInMethodsTask.isSuccessful) {
                                    val signInMethods = signInMethodsTask.result?.signInMethods
                                    if (signInMethods.isNullOrEmpty()) {
                                        // Verifica se la nuova email è già utilizzata da un altro utente nel database Firestore
                                        db.collection("users")
                                            .whereEqualTo("email", newEmail)
                                            .get()
                                            .addOnCompleteListener { querySnapshotTask ->
                                                if (querySnapshotTask.isSuccessful) {
                                                    val querySnapshot = querySnapshotTask.result
                                                    if (querySnapshot?.isEmpty == true) {
                                                        // Nessun utente utilizza già questa email, procedi con l'aggiornamento
                                                        val userId = user.uid
                                                        val docRef = db.collection("users").document(userId)

                                                        docRef.update(key, newEmail)
                                                            .addOnSuccessListener {
                                                                Log.d("Email Modificata", "successo nel Firestore")

                                                                //Aggiorna il Livedata per l'aggiornamento visuale nella UI della mail
                                                                getData()["email"]?.value = newEmail.trim()

                                                                // Dopo l'aggiornamento nel Firestore, aggiorna l'email nell'autenticazione
                                                                user.updateEmail(newEmail.trim())
                                                                    .addOnCompleteListener { updateEmailTask ->
                                                                        if (updateEmailTask.isSuccessful) {
                                                                            // L'aggiornamento dell'email nell'autenticazione è stato eseguito con successo
                                                                            Log.d("User", "Email aggiornata con successo nell'autenticazione")
                                                                            messageListener.showMessage("La nuova email è correttamente memorizzata")

                                                                        } else {
                                                                            // Gestisci l'errore di aggiornamento dell'email nell'autenticazione
                                                                            Log.e("User", "Errore durante l'aggiornamento dell'email nell'autenticazione", updateEmailTask.exception)
                                                                            messageListener.showMessage("Errore durante l'aggiornamento dell'email nell'autenticazione")

                                                                        }
                                                                    }
                                                            }
                                                            .addOnFailureListener { e ->
                                                                Log.d("Email Modificata", "fallito nel Firestore")
                                                                messageListener.showMessage("Errore durante l'aggiornamento dell'email")

                                                            }
                                                    } else {
                                                        // L'email è già utilizzata da un altro utente nel database Firestore
                                                        Log.e("User", "L'email è già in uso da un altro account nel database Firestore")
                                                        messageListener.showMessage("L'email è già in uso da un altro account")

                                                    }
                                                } else {
                                                    // Errore durante la verifica dell'email nel database Firestore
                                                    Log.e("User", "Errore durante la verifica dell'email nel database Firestore", querySnapshotTask.exception)
                                                    messageListener.showMessage("Errore durante l'aggiornamento dell'email")

                                                }
                                            }
                                    } else {
                                        // L'email è già utilizzata da un altro utente nel sistema di autenticazione
                                        Log.e("User", "L'email è già in uso da un altro account nel sistema di autenticazione")
                                        messageListener.showMessage("L'email è già in uso da un altro account")

                                    }
                                } else {
                                    // Errore durante la verifica dell'email nel sistema di autenticazione
                                    Log.e("User", "Errore durante la verifica dell'email nel sistema di autenticazione", signInMethodsTask.exception)
                                    messageListener.showMessage("Errore durante l'aggiornamento dell'email")
                                }
                            }
                    } else {
                        // Gestisci l'errore di riautenticazione
                        Log.e("User", "Errore durante la riautenticazione", reauthTask.exception)
                        messageListener.showMessage("Errore durante l'aggiornamento dell'email")

                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



}