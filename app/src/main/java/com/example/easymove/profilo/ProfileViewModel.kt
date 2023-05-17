package com.example.easymove.profilo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot

class ProfileViewModel: ViewModel() {
/*tutti i dati presi dalla repository --> creo un oggetto profileRepository*/

    private val repository = ProfileRepository()
    /*private val data: MutableLiveData<String?> = MutableLiveData()*/
    private val data: HashMap<String, MutableLiveData<String?>> = hashMapOf(
        "email" to MutableLiveData(),
        "name" to MutableLiveData(),
        "surname" to MutableLiveData()
    )

    fun fetchData() {
        repository.getDataFromFirestore().addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val result: DocumentSnapshot? = task.result
                val email = result?.getString("Email") // Sostituisci "your_field" con il nome del campo che desideri ottenere dal documento
                val name = result?.getString("name")
                val surname = result?.getString("surname")

                /*val dataFromDB = "Field 1: $email, Field 2: $name, Field 3: $surname"*/

                data["email"]?.value = email
                data["name"]?.value = name
                data["surname"]?.value = surname

            }
        })
    }

    fun getData(): HashMap<String, MutableLiveData<String?>> {
        return data
    }

}