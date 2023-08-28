package com.example.easymove.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.Recensione
import com.example.easymove.model.Veicolo
import com.example.easymove.repository.RecensioneRepository
import com.google.firebase.firestore.ListenerRegistration

class RecensioneViewModel: ViewModel() {

    val recensioneRepository = RecensioneRepository()

    private val _recensioniLiveData: MutableLiveData<List<Recensione>> = MutableLiveData()
    val recensioniLiveData: LiveData<List<Recensione>> = _recensioniLiveData
    private var recensioniListener: ListenerRegistration? = null


    fun creaRecensione(idCreatore:String, idRicevitore:String, stelline: String, descrizione: String, callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        if(stelline!="0.0" && descrizione.isNotEmpty()){
            recensioneRepository.storeRecensione(idCreatore, idRicevitore, stelline, descrizione){success, message->
                if(success){
                    callback(true, "Recensione Inviata")
                }else{
                    callback(false, message)
                }
            }
        } else{
        callback(false, "Compilare tutti i campi")
        }
    }

    fun startRecensioniListener() {
        recensioniListener = recensioneRepository.getRecensioniListener { success, error, veicoliList ->
            if (success) {
                _recensioniLiveData.postValue(veicoliList)
            } else {
                _recensioniLiveData.postValue(emptyList())
            }
        }
    }

    fun filterRecensioneByUserId(userId: String, recensioniList: List<Recensione>): ArrayList<Recensione> {
        val filteredList = recensioniList.filter { recensione -> recensione.idRicevitore == userId }
        return ArrayList(filteredList)
    }}