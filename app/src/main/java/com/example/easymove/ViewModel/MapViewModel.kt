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
    fun getAddressDetails(place: Place, originData: MapData, destinationData: MapData, focusOriginBool: Boolean){

        // Estrae dalla variabile place restituita dall'autocomplete addressComponents
        val addressComponents = place.addressComponents.asList()

        // Se il focus è sull'Origin editext riempie i campi dell'oggetto originData altrimenti di destinationData
        if(focusOriginBool){
            originData.address =place.address
            originData.latitude=place.latLng.latitude.toString()
            originData.longitude=place.latLng.longitude.toString()

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

    fun getAddressDetailsVeicolo(place: Place, positionData: MapData){

        // Estrae dalla variabile place restituita dall'autocomplete addressComponents
        val addressComponents = place.addressComponents.asList()

        positionData.address =place.address
        positionData.latitude=place.latLng.latitude.toString()
        positionData.longitude=place.latLng.longitude.toString()

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



    fun HttpRequestDirections(viewModelScope: CoroutineScope, originData: MapData, destinationData: MapData, apiKey: String, callback: (String)-> Unit) {
        viewModelScope.launch {
            val origin = "${originData.latitude},${originData.longitude}"
            val destination = "${destinationData.latitude},${destinationData.longitude}"

            val url = URL("https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$origin" +
                    "&destination=$destination" +
                    "&mode=driving" +
                    "&key=${apiKey}")

            withContext(Dispatchers.IO) {
                (url.openConnection() as? HttpURLConnection)?.run {
                    requestMethod = "GET"

                    val responseCode = responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
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
                        // Gestisci qui gli errori di richiesta
                    }

                    disconnect()
                }
            }
        }
    }

    fun parseDistanceTimeFromJson(jsonResponse: String): Pair<String, String> {
        val jsonObject = JSONObject(jsonResponse)
        val routesArray = jsonObject.getJSONArray("routes")

        if (routesArray.length() > 0) {
            val route = routesArray.getJSONObject(0)
            val legsArray = route.getJSONArray("legs")

            if (legsArray.length() > 0) {
                val leg = legsArray.getJSONObject(0)
                val distanceObject = leg.getJSONObject("distance")
                val distanceText = distanceObject.getString("text")

                val durationObject = leg.getJSONObject("duration")
                val durationText = durationObject.getString("text")

                return Pair(distanceText, durationText)
            }
        }

        return Pair("N/A","N/A") // Ritorna "N/A" se non è possibile estrarre la distanza
    }

 fun ParseRoute(jsonResponse: String): MutableList<LatLng> {
     val jsonObject = JSONObject(jsonResponse)
     val routesArray = jsonObject.getJSONArray("routes")
     if (routesArray.length() > 0) {
         val route = routesArray.getJSONObject(0)
         val overviewPolyline = route.getJSONObject("overview_polyline")
         val points = overviewPolyline.getString("points")

         return PolyUtil.decode(points)
     }
  return mutableListOf()
 }

}