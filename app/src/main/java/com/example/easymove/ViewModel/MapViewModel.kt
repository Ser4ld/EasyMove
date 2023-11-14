package com.example.easymove.ViewModel

import android.provider.Settings.Secure.getString
import android.text.Editable
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.easymove.R
import com.example.easymove.model.MapData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.Place
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.security.auth.callback.Callback

class MapViewModel: ViewModel() {

    // funzione che estrae i dettagli dell'indirizzo dall'oggetto Place restituito dall'autocomplete degli indirizzi.
    fun getAddressDetails(place: Place, originData: MapData, destinationData: MapData, focusOriginBool: Boolean){

        // Estrae dalla variabile place restituita dall'autocomplete gli addressComponents
        val addressComponents = place.addressComponents.asList()

        // Se il focus è sull'Origin EditText, riempie i campi dell'oggetto originData, altrimenti di destinationData
        if(focusOriginBool){
            originData.address =place.address
            originData.latitude=place.latLng.latitude.toString()
            originData.longitude=place.latLng.longitude.toString()

            // Itera sugli addressComponents per estrarre dettagli specifici
            for (component in addressComponents) {
                val types = component.types
                val name = component.name

                when {
                    "locality" in types -> originData.city = name
                    "administrative_area_level_2" in types -> originData.province= name
                    "administrative_area_level_1" in types -> originData.region = name
                    "country" in types -> originData.country = name
                    "postal_code" in types -> originData.postalCode = name
                }

            }

        } else  {

            destinationData.address=place.address
            destinationData.latitude=place.latLng.latitude.toString()
            destinationData.longitude=place.latLng.longitude.toString()

            // Itera sugli addressComponents per estrarre dettagli specifici
            for (component in addressComponents) {
                val types = component.types
                val name = component.name

                when {
                    "locality" in types -> destinationData.city = name
                    "administrative_area_level_2" in types -> destinationData.province= name
                    "administrative_area_level_1" in types -> destinationData.region = name
                    "country" in types -> destinationData.country = name
                    "postal_code" in types -> destinationData.postalCode = name
                }
            }
        }
    }

    // Funzione che estrae i dettagli dell'indirizzo da un oggetto Place restituito dall'autocompletamento.
    fun getAddressDetailsVeicolo(place: Place, positionData: MapData){

        // Estrae dalla variabile place restituita dall'autocomplete addressComponents
        val addressComponents = place.addressComponents.asList()

        // Salva l'indirizzo completo, la latitudine e la longitudine
        positionData.address =place.address
        positionData.latitude=place.latLng.latitude.toString()
        positionData.longitude=place.latLng.longitude.toString()

        // Analizza e salva i dettagli dell'indirizzo che ci interessano
        // (locality, administrative_area_level_2, etc.)
        for (component in addressComponents) {
            val types = component.types
            val name = component.name

            when {
                "locality" in types -> positionData.city = name
                "administrative_area_level_2" in types -> positionData.province= name
                "administrative_area_level_1" in types -> positionData.region = name
                "country" in types -> positionData.country = name
                "postal_code" in types -> positionData.postalCode = name
            }
        }
    }


    // Esegue una richiesta HTTP per ottenere le indicazioni stradali tra due posizioni utilizzando l'API di Google Maps.
    fun HttpRequestDirections(viewModelScope: CoroutineScope, originData: MapData, destinationData: MapData, apiKey: String, callback: (String)-> Unit) {
        viewModelScope.launch {

            // Costruisci le stringhe di latitudine e longitudine per origine e destinazione
            val origin = "${originData.latitude},${originData.longitude}"
            val destination = "${destinationData.latitude},${destinationData.longitude}"

            // Costruisci l'URL per la richiesta delle indicazioni stradali
            val url = URL("https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$origin" +
                    "&destination=$destination" +
                    "&mode=driving" +
                    "&key=${apiKey}")

            withContext(Dispatchers.IO) {
                // Apre la connessione HTTP
                (url.openConnection() as? HttpURLConnection)?.run {
                    // Imposta il metodo di richiesta GET
                    requestMethod = "GET"

                    // Ottiene il codice di risposta dalla richiesta
                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Legge la risposta e la passa alla callback
                        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()

                        var inputLine: String?
                        while (bufferedReader.readLine().also { inputLine = it } != null) {
                            response.append(inputLine)
                        }
                        bufferedReader.close()

                        val jsonResponse = response.toString()
                        Log.d("json", jsonResponse)
                        Log.d("json", jsonResponse)

                        callback(jsonResponse)
                    } else {

                    }

                    // Chiude la connessione
                    disconnect()
                }
            }
        }
    }

    // Analizza la risposta JSON ottenuta dalla richiesta delle indicazioni stradali di Google Maps.
    fun parseDistanceTimeFromJson(jsonResponse: String): Pair<String, String> {
        // Crea un oggetto JSON dalla stringa di risposta
        val jsonObject = JSONObject(jsonResponse)

        // Ottiene l'array di percorsi dalla risposta JSON
        val routesArray = jsonObject.getJSONArray("routes")

        // Verifica se ci sono percorsi disponibili
        if (routesArray.length() > 0) {

            // Ottiene il primo percorso dall'array
            val route = routesArray.getJSONObject(0)

            // Ottiene l'array di segmenti ("legs") dal percorso
            val legsArray = route.getJSONArray("legs")

            // Verifica se ci sono segmenti disponibili
            if (legsArray.length() > 0) {

                // Ottiene il primo segmento dall'array
                val leg = legsArray.getJSONObject(0)

                // Ottiene l'oggetto della distanza dal segmento
                val distanceObject = leg.getJSONObject("distance")

                // Ottiene la stringa della distanza
                val distanceText = distanceObject.getString("text")

                // Ottiene l'oggetto della durata dal segmento
                val durationObject = leg.getJSONObject("duration")

                // Ottiene la stringa della durata
                val durationText = durationObject.getString("text")

                // Ritorna una coppia di stringhe rappresentanti la distanza e il tempo di percorrenza
                return Pair(distanceText, durationText)
            }
        }
        // Ritorna "N/A" se non è possibile estrarre la distanza
        return Pair("N/A","N/A")
    }

    // Funzione che analizza la risposta JSON ottenuta dalla richiesta delle indicazioni stradali di Google Maps per ottenere
    // la lista di punti LatLng che rappresentano la linea del percorso
    fun ParseRoute(jsonResponse: String): MutableList<LatLng> {

        // Crea un oggetto JSON rappresentante la risposta di google maps
        val jsonObject = JSONObject(jsonResponse)

        // Ottiene l'array di percorsi dalla risposta JSON
        val routesArray = jsonObject.getJSONArray("routes")

        // Verifica se ci sono percorsi disponibili
        if (routesArray.length() > 0) {
            val route = routesArray.getJSONObject(0)

            // Ottiene l'oggetto "overview_polyline" dal percorso
            val overviewPolyline = route.getJSONObject("overview_polyline")

            // Ottiene la stringa "points" dalla polylinea
            val points = overviewPolyline.getString("points")

            // Decodifica la stringa "points" in una lista di oggetti LatLng
            return PolyUtil.decode(points)
        }
        // Ritorna una lista vuota se non è possibile estrarre la polyline del percorso
        return mutableListOf()
    }

}