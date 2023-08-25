package com.example.easymove.ViewModel

import android.os.Build
import android.text.BoringLayout
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.easymove.repository.RichiestaRepository
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
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
                if(checkDate(data)){
                    richiestaRepository.storeRequest(guidatoreId,consumatoreId,targaveicolo,puntoPartenza,puntoArrivo,data,descrizione, "inAttesa"){success,ErrMsg->
                        if(success){
                            callback(true, "Richiesta Inviata")
                        }else{
                            callback(false, ErrMsg)
                        }
                    }
                }else{
                    callback(false, "Data non valida, scegli una data successiva al giorno corrente")
                }

            }else{
                callback(false, "compila tutti i campi della richiesta")
            }
        }else{
            callback(false, "Errore di sistema")
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDate(stringDate: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val enteredDate = LocalDate.parse(stringDate, formatter)
        val currentDate = LocalDate.now()


        return (enteredDate.isAfter(currentDate))

    }

}