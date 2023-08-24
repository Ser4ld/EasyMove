package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.repository.RichiestaRepository
import javax.security.auth.callback.Callback

class RichiestaViewModel: ViewModel() {

    val richiestaRepository = RichiestaRepository()

    fun validateRichiesta(
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        data: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        if(guidatoreId.isNotEmpty() && consumatoreId.isNotEmpty() && targaveicolo.isNotEmpty() && puntoPartenza.isNotEmpty() && puntoArrivo.isNotEmpty()){
            if(data.isNotEmpty() && descrizione.isNotEmpty()){
                richiestaRepository.storeRequest(guidatoreId,consumatoreId,targaveicolo,puntoPartenza,puntoArrivo,data,descrizione){success,ErrMsg->
                if(success){
                    callback(true, "Richiesta Inviata")
                }else{
                    callback(false, ErrMsg)
                }
                }
            }else{
                callback(false, "compila tutti i campi della richiesta")
            }
        }else{
            callback(false, "Errore di sistema")
        }
    }

}