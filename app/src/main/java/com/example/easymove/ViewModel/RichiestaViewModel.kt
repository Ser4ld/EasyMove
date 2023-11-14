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
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.security.auth.callback.Callback
import kotlin.math.round
import kotlin.math.roundToInt

class RichiestaViewModel: ViewModel() {

    val richiestaRepository = RichiestaRepository()

    private val _richiesteLiveData: MutableLiveData<List<Richiesta>> = MutableLiveData()
    val richiesteLiveData: LiveData<List<Richiesta>> = _richiesteLiveData


    private var richiesteListener: ListenerRegistration? = null


    // Inoltra una richiesta al guidatore per la prenotazione del veicolo
    @RequiresApi(Build.VERSION_CODES.O)
    fun inoltraRichiesta(
        guidatoreId: String,
        consumatoreId: String,
        targaveicolo: String,
        puntoPartenza: String,
        puntoArrivo: String,
        prezzo: String,
        data: String,
        descrizione: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        // verifica che i parametri passati non siano vuoti
        if(guidatoreId.isNotEmpty() && consumatoreId.isNotEmpty() && targaveicolo.isNotEmpty() && puntoPartenza.isNotEmpty() && puntoArrivo.isNotEmpty() && prezzo.isNotEmpty()){
            if(data.isNotEmpty() && descrizione.isNotEmpty()){
                //verifica che la data immessa sia successiva a quella corrente tramite la funzione checkdate
                if(checkDate(data)){
                    // viene richiamato il metodo storerequest per memorizzare la richiesta nel firestore database
                    richiestaRepository.storeRequest(guidatoreId,consumatoreId,targaveicolo,puntoPartenza,puntoArrivo,data,descrizione, "Attesa", prezzo){success,ErrMsg->
                        if(success){
                            callback(true, "Richiesta Inviata")
                        }
                        //vengono gestite le varie eccezioni
                        else{
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

    // Aggiorna lo stato della richietra tramite il metodo updateRichiestaStato
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


    // Metodo per verificare che la data immessa sia successiva a quella corrente
    @RequiresApi(Build.VERSION_CODES.O)     // indica che il metodo richiede API di livello 26 o superiore
    fun checkDate(stringDate: String): Boolean {
        // Crea un formatter per il formato "dd/MM/yyyy"
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Converte la stringa della data in un oggetto LocalDate
        val enteredDate = LocalDate.parse(stringDate, formatter)

        // Ottiene la data corrente
        val currentDate = LocalDate.now()

        // Restituisce true se la data immessa è successiva alla data corrente
        return enteredDate.isAfter(currentDate)
    }


    // aggiorna lo stato delle richieste se queste sono in attesa e
    // se la data dell'appuntamento risulta antecedente alla data corrente
    // allora il loro stato verrà impostato come rifiutate
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

    // controlla che ci siano delle richieste nello stato di accettate riguardanti il veicolo
    fun checkRichiestaOnDeleteVeicolo(guidatoreId: String, richiesteList: List<Richiesta>, callback: (Boolean, String?) -> Unit) {
            val isCompletedFound = richiesteList?.any { richiesta ->
                richiesta.stato == "Accettata" && richiesta.guidatoreId == guidatoreId
            } ?: false

            callback(isCompletedFound, null)

    }

    // Aggiorna lo stato delle richieste in "Attesa" associate a un guidatore quando viene eliminato un veicolo,
    // lo stato di quest'ulime verrà impostato come "Rifiutata"
    fun updateRichiestaonDeleteVeicolo(guidatoreId: String, richiesteList: List<Richiesta>){
        richiesteList?.forEach { richiesta ->
                if(richiesta.guidatoreId == guidatoreId && richiesta.stato == "Attesa")
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

    // Verifica se la data fornita è uguale o precedente alla data corrente.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkDateAnnullaRichiesta(stringDate: String): Boolean{
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val enteredDate = LocalDate.parse(stringDate, formatter)
        val currentDate = LocalDate.now()

        return (currentDate.isEqual(enteredDate) || currentDate.isAfter(enteredDate))
    }

    // Verifica se è possibile completare una determinata richiesta
    // andando a controllare la data associata ad essa
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkClickOnComplete(richiesta: Richiesta): Boolean{
        if(!checkDate(richiesta.data)){
            return true
        }
        return false
    }


    // Verifica se è possibile annullare una determinata richiesta
    // andando a controllare la data associata ad essa
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkClickOnAnnulla(richiesta: Richiesta): Boolean{
        if(checkDateAnnullaRichiesta(richiesta.data)){
            return false
        }
        return true
    }


    // Avvia il listener per gli aggiornamenti sulla lista di richieste
    fun startRichiesteListener() {
        // Ottieni il listener dalla repository delle richieste
        richiesteListener = richiestaRepository.getRichiesteListener { success, error, richiestaList ->

            // Verifica se l'operazione ha avuto successo
            if (success) {

                // Aggiorna il LiveData con la lista di richieste ottenuta dalla repository
                _richiesteLiveData.postValue(richiestaList)
            } else {

                // Se l'operazione fallisce, imposta il LiveData con una lista vuota
                _richiesteLiveData.postValue(emptyList())
            }
        }
    }

    // Filtra le richieste totali in base all'ID dell'utente e al tipo di utente se è guidatore
    // filtra la lista in base alle richieste ricevute se invece è consumatore filtra le richieste in base
    // a quelle che ha mandato
    fun filterRichiesteByUserId(userId: String, richiesteList: List<Richiesta>, userType: String): ArrayList<Richiesta> {
        if(userType == "guidatore"){
            val filteredList = richiesteList.filter { richiesta -> richiesta.guidatoreId == userId }
            return ArrayList(filteredList)
        }else{
            val filteredList = richiesteList.filter { richiesta -> richiesta.consumatoreId == userId }
            return ArrayList(filteredList)
        }

    }


    // Funzione che ottiene le richieste in relazione allo stato e all'utente
    fun filterRichiesteByUserIdAndStato(userId: String, stato: String, richiesteList: List<Richiesta>,userType: String): List<Richiesta> {
        if(userType == "guidatore"){
            val filteredList = richiesteList.filter { richiesta -> richiesta.guidatoreId == userId && richiesta.stato == stato }
            return filteredList
        }else{
            val filteredList = richiesteList.filter { richiesta -> richiesta.consumatoreId == userId && richiesta.stato == stato }
            return filteredList
        }

    }

    // Funzione che restituisce il numero delle richieste totali
    fun totaleRichieste( richiesteList: List<Richiesta>): Int {
        return richiesteList.size
    }


    fun getAcceptedRequestDatesForGuidatore(guidatoreId: String, richiesteList: List<Richiesta>): List<String> {
        val acceptedDates = mutableListOf<String>()
        val acceptedRichieste = richiesteList.filter { it.guidatoreId == guidatoreId && it.stato == "Accettata" }
        for (richiesta in acceptedRichieste) {
            acceptedDates.add(richiesta.data)
        }
        return acceptedDates
    }


    //Calcola il prezzo del viaggio in base alla distanza e alla tariffa per chilometro specificata
    fun calcolaPrezzo(input: String, tariffakm: String): Double {
        val pattern = Pattern.compile("\\d+(\\.\\d+)?") // Crea un pattern per trovare sequenze di numeri
        val matcher = pattern.matcher(input)

        if (matcher.find()) {
            var prezzo= (matcher.group().toDouble()*tariffakm.toDouble()) // Converte la sequenza di numeri in un intero
            return Math.round(prezzo * 100.0) / 100.0
        }

        return 0.0 // Ritorna un valore di default se non trova alcun numero
    }



}