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

    fun updateRichiestaStato(richiestaId: String, nuovoStato: String, callback: (Boolean, String?) -> Unit) {
        richiestaRepository.updateRichiestaStato(richiestaId, nuovoStato) { success, message ->
            if(success) {
                callback(true,message)
            }
            else {
                callback(true,message)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDate(stringDate: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val enteredDate = LocalDate.parse(stringDate, formatter)
        val currentDate = LocalDate.now()


        return (enteredDate.isAfter(currentDate))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndUpdateStato() {
        richiesteLiveData.observeForever {richiesteList->
            richiesteList?.forEach { richiesta ->
                if(!checkDate(richiesta.data) && richiesta.stato== "Attesa")
                {
                    richiestaRepository.updateRichiestaStato(richiesta.richiestaId, "Rifiutata"){success, message ->
                        if(success){
                            Log.i("Richieste", "Richieste aggiornate")
                        }else{
                            Log.i("Richieste", "Errore aggiornamento richieste")
                        }
                    }
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDateAnnullaRichiesta(stringDate: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val enteredDate = LocalDate.parse(stringDate, formatter)
        val currentDate = LocalDate.now()

        return currentDate.isEqual(enteredDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkClickOnComplete(richiesta: Richiesta): Boolean{
        if(!checkDate(richiesta.data)){
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkClickOnAnnulla(richiesta: Richiesta): Boolean{
        if(checkDateAnnullaRichiesta(richiesta.data)){
            return false
        }
        return true
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

    fun filterRichiesteByUserId(userId: String, richiesteList: List<Richiesta>, userType: String): ArrayList<Richiesta> {
        if(userType == "guidatore"){
            val filteredList = richiesteList.filter { richiesta -> richiesta.guidatoreId == userId }
            return ArrayList(filteredList)
        }else{
            val filteredList = richiesteList.filter { richiesta -> richiesta.consumatoreId == userId }
            return ArrayList(filteredList)
        }

    }

    fun filterRichiesteByUserIdAndStato(userId: String, stato: String, richiesteList: List<Richiesta>,userType: String): List<Richiesta> {
        if(userType == "guidatore"){
            val filteredList = richiesteList.filter { richiesta -> richiesta.guidatoreId == userId && richiesta.stato == stato }
            return filteredList
        }else{
            val filteredList = richiesteList.filter { richiesta -> richiesta.consumatoreId == userId && richiesta.stato == stato }
            return filteredList
        }

    }




    fun totaleRichieste( richiesteList: List<Richiesta>): Int {
        return richiesteList.size
    }




}