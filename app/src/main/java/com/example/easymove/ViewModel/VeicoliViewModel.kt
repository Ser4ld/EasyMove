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


    fun checkModificaVeicolo(veicolo: Veicolo, imageuri: Uri?, Locazione: String, TariffaKm: String,positionData: MapData ,callback: (Boolean, String?) -> Unit){
        if(Locazione.isNotEmpty() && TariffaKm.isNotEmpty()){
            veicolo.tariffakm = TariffaKm
            if(positionData.address != "" && positionData.city != "" && positionData.postalCode != ""){
                veicolo.via= positionData.address
                veicolo.citta = positionData.city
                veicolo.codicePostale = positionData.postalCode
                veicolo.latitude=positionData.latitude
                veicolo.longitude=positionData.longitude
            }
            veicoliRepository.updateVeicolo(veicolo,imageuri){success, message ->
                if(success){
                    callback(true, message)
                }else{
                    callback(false, message)
                }
            }
        }else{
            callback(false, "i campi Locazione e Tariffa non possono essere vuoti")
        }
    }
    fun storeVehicle(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via: String,
        CodicePostale: String,
        latitude:String,
        longitude:String,
        AltezzaCassone: String,
        LunghezzaCassone: String,
        LarghezzaCassone: String,
        TariffaKm: String,
        imageUri: Uri?,
        viewModelScope: CoroutineScope,
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            if (UserId.isNotEmpty() && NomeVeicolo.isNotEmpty() && Targa.isNotEmpty() && CittaMezzo.isNotEmpty() && AltezzaCassone.isNotEmpty() && LunghezzaCassone.isNotEmpty() && LarghezzaCassone.isNotEmpty() && TariffaKm.isNotEmpty()) {
                if(isValidItalianLicensePlate(Targa)) {
                    if (imageUri != null) {
                        val checktarga = veicoliRepository.checkTargaExists(Targa)

                        if (checktarga) {
                            callback(false, "Targa già esistente")
                            Log.d("TARGACHECK", "check")
                        } else {
                            val Capienza = calcoloCapienza(
                                LunghezzaCassone.toDouble(),
                                AltezzaCassone.toDouble(),
                                LarghezzaCassone.toDouble()
                            )
                            Log.d("TARGACHECK", "not check")
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
                                TariffaKm,
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
                        callback(false, "Inserisci un immagine per continuare")
                    }
                }else {
                    callback(false, "Formato targa errato (AA123BB)")
                }
            } else {
                callback(false, "Tutti i campi devono essere compilati")
            }
        }
    }


    fun calcoloCapienza(lunghezza: Double, altezza: Double, larghezza: Double): String {
        val capienza = ((lunghezza * altezza * larghezza)/1000000)
        val formattedCapienza = String.format("%.2f", capienza)
        return "$formattedCapienza m³"
    }


    fun isValidItalianLicensePlate(targa: String): Boolean {
        // Validazione targhe italiane
        val regex = Regex("^[A-Z]{2}\\d{3}[A-Z]{2}$")
        return regex.matches(targa)
    }

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


    fun startVeicoliListener() {
        veicoliListener = veicoliRepository.getVeicoliListener { success, error, veicoliList ->
            if (success) {
                _veicoliLiveData.postValue(veicoliList)
            } else {
                _veicoliLiveData.postValue(emptyList())
            }
        }
    }

    fun onVeicoloClicked(position: Int) {
        _richiestaClickedEvent.value = position
    }

    fun resetRichiestaClickedEvent() {
        _richiestaClickedEvent.value = -1
    }

    override fun onCleared() {
        super.onCleared()
        veicoliListener?.remove()
    }

    fun filterVeicoliByUserId(userId: String, veicoliList: List<Veicolo>): ArrayList<Veicolo> {
        val filteredList = veicoliList.filter { veicolo -> veicolo.id == userId }
        return ArrayList(filteredList)
    }


    fun countVeicoliByUserId(userId: String, veicoliList: List<Veicolo>): Int {
        return filterVeicoliByUserId(userId, veicoliList).size
    }

    fun filterVeicoliByCittaAndCodicePostale(citta: String, codicePostale: String, veicoliList: List<Veicolo>): ArrayList<Veicolo> {
        val filteredList = veicoliList.filter { veicolo -> veicolo.citta == citta && veicolo.codicePostale == codicePostale }
        return ArrayList(filteredList)
    }


    fun filterListbyTarga(targa: String, veicoliList: List<Veicolo>): Veicolo?{
        return veicoliList.find { it.targa == targa }
    }


    fun getCoordinatesForDriverVehicles( veicoliList: List<Veicolo>): List<Pair<String, String>> {
        val coordinatesList = mutableListOf<Pair<String, String>>()
        for (veicolo in veicoliList) {
            coordinatesList.add(Pair(veicolo.latitude, veicolo.longitude))
        }
        return coordinatesList
    }

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