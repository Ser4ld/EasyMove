package com.example.easymove.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.easymove.model.MapData
import com.example.easymove.model.Veicolo
import com.example.easymove.repository.VeicoliRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class VeicoliViewModel: ViewModel() {
    private var veicoliRepository = VeicoliRepository()

    private val _veicoliLiveData: MutableLiveData<List<Veicolo>> = MutableLiveData()
    val veicoliLiveData: LiveData<List<Veicolo>> = _veicoliLiveData
    //_veicoloClickedEvent LiveData utilizzato per gestire l'evento di click su un elemento del MyAdapterVeicoli
    private val _richiestaClickedEvent = MutableLiveData<Int>()
    val richiestaClickedEvent: LiveData<Int> = _richiestaClickedEvent

    private var veicoliListener: ListenerRegistration? = null


    // Verifica e gestisce la modifica del veicolo con i dati forniti.
    fun checkModificaVeicolo(veicolo: Veicolo, imageuri: Uri?, Locazione: String, TariffaKm: String,positionData: MapData ,callback: (Boolean, String?) -> Unit){
        if(Locazione.isNotEmpty() && TariffaKm.isNotEmpty()){
            val tariffaKmDouble = TariffaKm.toDoubleOrNull()
            if (tariffaKmDouble!=null){
                // controlla che la tariffa immessa sia maggiore di 0
                if (tariffaKmDouble>0){

                    // Aggiorna i dati del veicolo con quelli forniti
                    veicolo.tariffakm = tariffaKmDouble.toString()

                    // Aggiorna la posizione solo se sono disponibili dati validi
                    if(positionData.address != "" && positionData.city != "" && positionData.postalCode != ""){
                        veicolo.via= positionData.address
                        veicolo.citta = positionData.city
                        veicolo.codicePostale = positionData.postalCode
                        veicolo.latitude=positionData.latitude
                        veicolo.longitude=positionData.longitude
                    }

                    // Chiama il repository per l'aggiornamento del veicolo nel database
                    veicoliRepository.updateVeicolo(veicolo,imageuri){success, message ->
                        if(success){
                            callback(true, message)
                        }else{
                            callback(false, message)
                        }
                    }
                }else{
                    callback(false, "Inserire numero positivo per tariffa")
                }
            }else{
                callback(false, "Inserire numero valido per tariffa")
            }



        }else{
            callback(false, "I campi Locazione e Tariffa non possono essere vuoti")
        }
    }


    // Registra un nuovo veicolo
    fun storeVehicle(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via: String,
        CodicePostale: String,
        latitude: String,
        longitude: String,
        AltezzaCassone: String,
        LunghezzaCassone: String,
        LarghezzaCassone: String,
        TariffaKm: String,
        imageUri: Uri?,
        viewModelScope: CoroutineScope,
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            // Verifica se tutti i campi necessari sono stati compilati
            if (UserId.isNotEmpty() && NomeVeicolo.isNotEmpty() && Targa.isNotEmpty() && CittaMezzo.isNotEmpty()) {
                if (AltezzaCassone.isNotEmpty() && LunghezzaCassone.isNotEmpty() && LarghezzaCassone.isNotEmpty() && TariffaKm.isNotEmpty()) {
                    // Converte i valori numerici e verifica che siano validi
                    val altezzaCassoneDouble = AltezzaCassone.toDoubleOrNull()
                    val lunghezzaCassoneDouble = LunghezzaCassone.toDoubleOrNull()
                    val larghezzaCassoneDouble = LarghezzaCassone.toDoubleOrNull()
                    val tariffaKmDouble = TariffaKm.toDoubleOrNull()

                    // verifica che i valori non siano nulli
                    if (altezzaCassoneDouble != null && lunghezzaCassoneDouble != null && larghezzaCassoneDouble != null && tariffaKmDouble != null) {

                        // verifica che i valori altezzaCassoneDouble, lunghezzaCassoneDouble,
                        // etc. siano maggiori di 0
                        if (altezzaCassoneDouble > 0 && lunghezzaCassoneDouble > 0 && larghezzaCassoneDouble > 0 && tariffaKmDouble > 0) {

                            // Verifica il formato della targa tramite il metodo isValidItalianLicensePlate
                            if (isValidItalianLicensePlate(Targa)) {
                                if (imageUri != null) {

                                    // Verifica che la targa immessa non esista già all'interno del database
                                    //tramite il metodo checkTargaExists
                                    val checktarga = veicoliRepository.checkTargaExists(Targa)
                                    if (checktarga) {
                                        callback(false, "Targa giÃ  esistente")
                                        Log.d("TARGACHECK", "check")
                                    } else {
                                        // se non è presente nessun veicolo registrato con la stessa targa
                                        // si calcola la capienza del furgone tramite il metodo calcoloCapienza
                                        val Capienza = calcoloCapienza(
                                            lunghezzaCassoneDouble,
                                            altezzaCassoneDouble,
                                            larghezzaCassoneDouble
                                        )
                                        Log.d("TARGACHECK", "not check")
                                        // Registra il veicolo nel repository tramite la funzione storeVehicleForUser
                                        veicoliRepository.storeVehicleForUser(
                                            UserId,
                                            NomeVeicolo,
                                            Targa,
                                            CittaMezzo,
                                            Via,
                                            CodicePostale,
                                            latitude,
                                            longitude,
                                            Capienza,
                                            tariffaKmDouble.toString(),
                                            imageUri
                                        ) { success, message ->
                                            if (success) {
                                                callback(true, "Veicolo registrato correttamente")
                                            } else {
                                                callback(false, message)
                                            }
                                        }
                                    }
                                } else {
                                    callback(false, "Inserisci un'immagine per continuare")
                                }
                            } else {
                                callback(false, "Formato targa errato (AA123BB)")
                            }
                        } else {
                            callback(false, "Altezza, lunghezza, larghezza e tariffa devono essere maggiori di 0")
                        }
                    } else {
                        callback(false, "Altezza, lunghezza, larghezza e tariffa devono essere valori numerici validi")
                    }
                } else {
                    callback(false, "Tutti i campi devono essere compilati")
                }
            } else {
                callback(false, "Tutti i campi devono essere compilati")
            }
        }
    }


    fun validateNumbers(numero: String): Boolean {
        val parti = numero.split(".")

        if (parti.size != 2) {
            return false
        }

        val parteSinistra = parti[0]
        val parteDestra = parti[1]

        if (parteSinistra.isEmpty() || parteDestra.isEmpty()) {
            return false
        }

        if (!parteSinistra.all { it.isDigit() } || !parteDestra.all { it.isDigit() }) {
            return false
        }

        return true
    }


    //Funzione che calcola la capienza del furgone in metri cubi
    fun calcoloCapienza(lunghezza: Double, altezza: Double, larghezza: Double): String {
        val capienza = ((lunghezza * altezza * larghezza)/1000000)
        val formattedCapienza = String.format("%.2f", capienza)
        return "$formattedCapienza m³"
    }

    // funzione di validazione della targa secondo un determinato pattern espresso con il regex
    fun isValidItalianLicensePlate(targa: String): Boolean {
        // Validazione targhe italiane
        val regex = Regex("^[A-Z]{2}\\d{3}[A-Z]{2}$")
        return regex.matches(targa)
    }

    // Funzione che permette di eliminatre il veicolo tramite la funzione deleteVeicolo
    fun deleteVeicolo(veicoloId: String, callback: (Boolean, String?) -> Unit) {
        // Chiamare la funzione deleteVeicolo dal repository o data access object
        veicoliRepository.deleteVeicolo(veicoloId){success,message ->
            if(success){
                callback(true, message)
            }else{
                callback(false, message)
            }
        }
    }


    // Avvia il listener per gli aggiornamenti sulla lista di veicoli
    fun startVeicoliListener() {

        // Ottieni il listener dalla repository dei veicoli tramite il metodo getVeicoliListener se l'operazione
        // ha avuto successo aggiorna il LiveData con la lista di veicoli ottenuta dalla repository se l'operazione fallisce,
        // imposta il LiveData con una lista vuota
        veicoliListener = veicoliRepository.getVeicoliListener { success, error, veicoliList ->
            if (success) {
                _veicoliLiveData.postValue(veicoliList)
            } else {
                _veicoliLiveData.postValue(emptyList())
            }
        }
    }

    // Metodo chiamato quando un veicolo viene cliccato nella RecyclerView
    fun onVeicoloClicked(position: Int) {
        // Imposta il valore dell'evento di clic con la posizione del veicolo cliccato
        _richiestaClickedEvent.value = position
    }

    fun resetRichiestaClickedEvent() {
        _richiestaClickedEvent.value = -1
    }

    override fun onCleared() {
        super.onCleared()
        veicoliListener?.remove()
    }

    // Funzione che filtra la lista di veicoli in base all'id dell'utente (l'id del veicolo è lo stesso dell'id utente)
    fun filterVeicoliByUserId(userId: String, veicoliList: List<Veicolo>): ArrayList<Veicolo> {
        val filteredList = veicoliList.filter { veicolo -> veicolo.id == userId }
        return ArrayList(filteredList)
    }

    // Funzione che filtra i veicoli in base all'utente e restituisce il numero di veicoli all'interno della lista
    fun countVeicoliByUserId(userId: String, veicoliList: List<Veicolo>): Int {
        return filterVeicoliByUserId(userId, veicoliList).size
    }

    // Funzione che filtra la lista dei veicoli in base alla città e al codice postale specificati.
    fun filterVeicoliByCittaAndCodicePostale(citta: String, codicePostale: String, veicoliList: List<Veicolo>): ArrayList<Veicolo> {

        // Utilizza la funzione filter per ottenere solo i veicoli con la città e il codice postale immessi
        val filteredList = veicoliList.filter { veicolo -> veicolo.citta == citta && veicolo.codicePostale == codicePostale }

        // Converte la lista filtrata in un ArrayList prima di restituirla
        return ArrayList(filteredList)
    }


    // funzione che permettere di filtrare la lista di veicoli e trovare quello con
    // la targa uguale al parametro passato alla funzione
    fun filterListbyTarga(targa: String, veicoliList: List<Veicolo>): Veicolo?{
        return veicoliList.find { it.targa == targa }
    }

    // Ottiene le coordinate dei veicoli dalla lista di veicoli.
    fun getCoordinatesForDriverVehicles( veicoliList: List<Veicolo>): List<Pair<String, String>> {
        val coordinatesList = mutableListOf<Pair<String, String>>()

        // Itera attraverso la lista di veicoli
        for (veicolo in veicoliList) {
            // Aggiunge la coppia di coordinate (latitudine e longitudine) alla lista
            coordinatesList.add(Pair(veicolo.latitude, veicolo.longitude))
        }

        // Restituisce la lista di coordinate dei veicoli
        return coordinatesList
    }

    // Verifica se è presente nella lista di veicoli uno con la targa uguale a quella che è stata passata come parametro
    fun verificaTargaPresente(targaDaVerificare: String, listaVeicoli: List<Veicolo>): Boolean {
        for (veicolo in listaVeicoli) {
            if (veicolo.targa == targaDaVerificare) {
                return true
            }
        }
        return false
    }

    fun sortVeicoliByParameter(parameter: String, veicoliList: List<Veicolo>): List<Veicolo> {

        return when (parameter) {
            "Modello" -> veicoliList.sortedBy { it.modello }
            "Capienza" -> veicoliList.sortedBy { it.capienza }
            "Tariffa" -> veicoliList.sortedBy { it.tariffakm }
            else -> veicoliList.sortedBy { it.modello } // Default "Modello"
        }
    }

}