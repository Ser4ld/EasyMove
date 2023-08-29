package com.example.easymove.ViewModel

import android.os.Build
import android.text.BoringLayout
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.Recensione
import com.example.easymove.model.Richiesta
import com.example.easymove.repository.RichiestaRepository
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.security.auth.callback.Callback

class RichiestaViewModel: ViewModel() {

    val richiestaRepository = RichiestaRepository()

    private val _richiesteLiveData: MutableLiveData<List<Richiesta>> = MutableLiveData()
    val richiesteLiveData: LiveData<List<Richiesta>> = _richiesteLiveData


    private var richiesteListener: ListenerRegistration? = null


    @RequiresApi(Build.VERSION_CODES.O)
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
                    richiestaRepository.storeRequest(guidatoreId,consumatoreId,targaveicolo,puntoPartenza,puntoArrivo,data,descrizione, "Attesa"){success,ErrMsg->
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

    fun startRichiesteListener() {
        richiesteListener = richiestaRepository.getRichiesteListener { success, error, richiestaList ->
            if (success) {
                _richiesteLiveData.postValue(richiestaList)
            } else {
                _richiesteLiveData.postValue(emptyList())
            }
        }
    }

    fun filterRichiesteByUserId(userId: String, richiesteList: List<Richiesta>): ArrayList<Richiesta> {
        val filteredList = richiesteList.filter { recensione -> recensione.guidatoreId == userId }
        return ArrayList(filteredList)
    }

    fun filterRichiesteByUserIdAndStato(userId: String, stato: String, richiesteList: List<Richiesta>): List<Richiesta> {
        val filteredList = richiesteList.filter { richiesta ->
            richiesta.guidatoreId == userId && richiesta.stato == stato
        }
        return filteredList
    }


    fun totaleRichieste( richiesteList: List<Richiesta>): Int {
        return richiesteList.size
    }




}