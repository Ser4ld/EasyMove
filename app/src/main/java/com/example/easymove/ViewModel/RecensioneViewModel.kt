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


    fun creaRecensione(richiestaId: String,consumatoreId:String, guidatoreId:String, valutazione: String, descrizione: String, callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        if(valutazione!="0.0" && descrizione.isNotEmpty()){
            recensioneRepository.storeRecensione(richiestaId,consumatoreId, guidatoreId, valutazione, descrizione){success, message->
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



    fun filterRecensioneByUserId(userId: String, recensioniList: List<Recensione>, userType: String): ArrayList<Recensione> {
        if(userType == "guidatore"){
            val filteredList = recensioniList.filter { recensione -> recensione.guidatoreId == userId }
            return ArrayList(filteredList)
        }else{
            val filteredList = recensioniList.filter {  recensione -> recensione.consumatoreId == userId }
            return ArrayList(filteredList)
        }
    }


    fun mediaRecensioniFiltrate(userId:String, recensioniList: List<Recensione>, userType: String): Float {
        val recensioniFiltrate = filterRecensioneByUserId(userId, recensioniList, userType)

        val sommaRecensioni = recensioniFiltrate.map { it.valutazione.toFloat() }.sum()

        val mediaRecensioni = if (recensioniFiltrate.isNotEmpty()) {
            sommaRecensioni / recensioniFiltrate.size.toFloat()
        } else {
            0.0f
        }
        return mediaRecensioni
    }

    fun totaleRecensioniFiltrate(userId:String, recensioniList: List<Recensione>, userType: String): Int {
        val recensioniFiltrate = filterRecensioneByUserId(userId, recensioniList, userType)
        return recensioniFiltrate.size
    }

    fun chcekRecensione(richiestaId: String, callback: (Boolean) -> Unit) {
        recensioniLiveData.observeForever { recensioniList ->
            recensioniList?.forEach { recensione ->
                if (recensione.richiestaId == richiestaId) {
                    callback(true)
                    return@forEach
                }
            }
            callback(false)
        }
    }

}