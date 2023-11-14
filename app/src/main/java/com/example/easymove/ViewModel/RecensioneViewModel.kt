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


    // Crea e invia una recensione per una richiesta.
    fun creaRecensione(richiestaId: String,consumatoreId:String, guidatoreId:String, valutazione: String, descrizione: String, callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        // Verifica che la valutazione non sia "0.0" e che la descrizione non sia vuota
        if(valutazione!="0.0" && descrizione.isNotEmpty()){

            // Chiamata alla repository per memorizzare la recensione
            recensioneRepository.storeRecensione(richiestaId,consumatoreId, guidatoreId, valutazione, descrizione){success, message->
                if(success){
                    // Chiamata di callback in caso di successo
                    callback(true, "Recensione Inviata")
                }else{
                    // Chiamata di callback in caso di fallimento, passando il messaggio di errore
                    callback(false, message)
                }
            }
        } else{
            // Chiamata di callback in caso di dati non validi o mancanti
            callback(false, "Compilare tutti i campi")
        }
    }

    // Avvia il listener per gli aggiornamenti sulla lista di recensioni
    fun startRecensioniListener() {

        // Ottieni il listener dalla repository delle recensioni
        recensioniListener = recensioneRepository.getRecensioniListener { success, error, veicoliList ->
            // Verifica se l'operazione ha avuto successo
            if (success) {

                // Aggiorna il LiveData con la lista di recensioni ottenuta dalla repository
                _recensioniLiveData.postValue(veicoliList)
            } else {

                // Se l'operazione fallisce, imposta il LiveData con una lista vuota
                _recensioniLiveData.postValue(emptyList())
            }
        }
    }


    // funzione che controlla prima il tipo dell'utente, se questo è un guidatore
    // filtra la lista con le recensioni rilasciate all'utente mentre se questo è consumatore la lista viene filtrata
    // secondo le recensioni che ha lasciato l'utente
    fun filterRecensioneByUserId(userId: String, recensioniList: List<Recensione>, userType: String): ArrayList<Recensione> {
        if(userType == "guidatore"){
            val filteredList = recensioniList.filter { recensione -> recensione.guidatoreId == userId }
            return ArrayList(filteredList)
        }else{
            val filteredList = recensioniList.filter {  recensione -> recensione.consumatoreId == userId }
            return ArrayList(filteredList)
        }
    }

    // funzione che calcola la valutazione media di un guidatore
    fun mediaRecensioniFiltrate(userId:String, recensioniList: List<Recensione>, userType: String): Float {
        // Filtra le recensioni in base all'utente e al tipo di utente
        val recensioniFiltrate = filterRecensioneByUserId(userId, recensioniList, userType)

        // Calcola la somma delle valutazioni delle recensioni filtrate
        val sommaRecensioni = recensioniFiltrate.map { it.valutazione.toFloat() }.sum()

        // Calcola la media delle valutazioni, gestendo il caso in cui non ci siano recensioni tramite la lambda function
        val mediaRecensioni = if (recensioniFiltrate.isNotEmpty()) {
            sommaRecensioni / recensioniFiltrate.size.toFloat()
        } else {
            0.0f
        }
        return mediaRecensioni
    }

    // Restituisce il numero di recensioni riguardanti uno specifico utente
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