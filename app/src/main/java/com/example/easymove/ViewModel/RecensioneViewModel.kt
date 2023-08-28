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


    fun creaRecensione(consumatoreId:String, guidatoreId:String, valutazione: String, descrizione: String, callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        if(valutazione!="0.0" && descrizione.isNotEmpty()){
            recensioneRepository.storeRecensione(consumatoreId, guidatoreId, valutazione, descrizione){success, message->
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
        val filteredList = recensioniList.filter { recensione -> recensione.guidatoreId == userId }
        return ArrayList(filteredList)
    }


    fun mediaRecensioniFiltrate(userId:String, recensioniList: List<Recensione>): Float {
        val recensioniFiltrate = filterRecensioneByUserId(userId, recensioniList)

        val sommaRecensioni = recensioniFiltrate.map { it.valutazione.toFloat() }.sum()

        val mediaRecensioni = if (recensioniFiltrate.isNotEmpty()) {
            sommaRecensioni / recensioniFiltrate.size.toFloat()
        } else {
            0.0f
        }
        return mediaRecensioni
    }

    fun totaleRecensioniFiltrate(userId:String, recensioniList: List<Recensione>): Int {
        val recensioniFiltrate = filterRecensioneByUserId(userId, recensioniList)
        return recensioniFiltrate.size
    }

}