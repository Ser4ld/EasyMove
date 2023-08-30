package com.example.easymove.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun storeVehicle(
        UserId: String,
        NomeVeicolo: String,
        Targa: String,
        CittaMezzo: String,
        Via: String,
        CodicePostale: String,
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


    private fun calcoloCapienza(lunghezza: Double, altezza: Double, larghezza: Double): String {
        val capienza = ((lunghezza * altezza * larghezza)/1000000)
        val formattedCapienza = String.format("%.2f", capienza)
        return "$formattedCapienza m³"
    }


    fun isValidItalianLicensePlate(targa: String): Boolean {
        // Validazione targhe italiane
        val regex = Regex("^[A-Z]{2}\\d{3}[A-Z]{2}$")
        return regex.matches(targa)
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
        val filteredList = veicoliList.filter { veicolo -> veicolo.citta == citta }
        return ArrayList(filteredList)
    }


    fun filterListbyTarga(targa: String, veicoliList: List<Veicolo>): Veicolo?{
        return veicoliList.find { it.targa == targa }
    }

}