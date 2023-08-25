package com.example.easymove.ViewModel

import androidx.lifecycle.ViewModel
import com.example.easymove.repository.RichiestaRepository
import java.util.UUID
import javax.security.auth.callback.Callback

class RichiestaViewModel: ViewModel() {

    val richiestaRepository = RichiestaRepository()

    fun inoltraRichiesta(
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
                richiestaRepository.storeRequest(generateCustomId(),guidatoreId,consumatoreId,targaveicolo,puntoPartenza,puntoArrivo,data,descrizione, "inAttesa"){success,ErrMsg->
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


    fun generateCustomId(): String {
        return UUID.randomUUID().toString()
    }

}