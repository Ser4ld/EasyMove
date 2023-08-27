package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.easymove.R
import com.example.easymove.databinding.FragmentHomeBinding
import com.example.easymove.model.MapData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject



class HomeFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap

    private var focusOriginBool: Boolean= false
    private var focusDestinationBool: Boolean= false


    private var originData: MapData = MapData()
    private var destinationData: MapData = MapData()


    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    getAddressDetails(place)
                        var messaggio =
                            "Indirizzo: $originData ,${originData.address}, Città: ${originData.city}, CAP: ${originData.postalCode}, Provincia: ${originData.province}, Regione: ${originData.region}, Nazione: ${originData.country}, Coordinate: (${originData.latitude},${originData.longitude}) "
                        Log.i("Prova", messaggio)

                        var messaggio2 =
                            "Indirizzo: $destinationData ${destinationData.address}, Città: ${destinationData.city}, CAP: ${destinationData.postalCode}, Provincia: ${destinationData.province}, Regione: ${destinationData.region}, Nazione: ${destinationData.country}, Coordinate: (${destinationData.latitude},${destinationData.longitude}) "
                        Log.i("Prova2", messaggio2)

                    if ( binding.editTextOrigin.text.toString().isNotEmpty() && binding.editTextDestination.text.toString().isNotEmpty()) {
                        HttpRequestDirections()
                    }

                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("Prova", "User canceled autocomplete")
            }
        }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza l'SDK
        Places.initialize(requireContext(), getString(R.string.map_api_key) )

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf( Place.Field.NAME, Place.Field.ADDRESS,  Place.Field.ADDRESS_COMPONENTS,Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())

        binding.editTextOrigin.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusOriginBool=true
                startAutocomplete.launch(intent)
                binding.editTextOrigin.clearFocus()
                binding.editTextOrigin.isCursorVisible = false

            }
        }

        binding.editTextDestination.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusDestinationBool=true
                startAutocomplete.launch(intent)
                binding.editTextDestination.clearFocus()
                binding.editTextDestination.isCursorVisible = false

            }
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        binding.button5.setOnClickListener{


        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordinate del Polo Montedago di Ancona
        val montedagoAncona = LatLng(43.608973, 13.512643)

        // Aggiunge il marker sulle coordinate passate in ingresso
        mMap.addMarker(MarkerOptions()
            .position(montedagoAncona)
            .title("Polo Montedago, Ancona"))

        val zoomLevel = 12.0f // Imposta il livello di zoom

        // Definisce la posizione e lo zoom della camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(montedagoAncona, zoomLevel))    }

    private fun getAddressDetails(place: Place){

        val addressComponents = place.addressComponents.asList()

        if(focusOriginBool){
            originData.address =place.address

            binding.editTextOrigin.text=Editable.Factory.getInstance().newEditable(originData.address)

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

                focusOriginBool=false
            }


        } else if (focusDestinationBool) {

            destinationData.address=place.address

            binding.editTextDestination.text=Editable.Factory.getInstance().newEditable(destinationData.address)

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
            focusDestinationBool=false
        }
    }

    private fun HttpRequestDirections() {
        lifecycleScope.launch {
            val origin = "${originData.latitude},${originData.longitude}"
            val destination = "${destinationData.latitude},${destinationData.longitude}"

            val url = URL("https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=$origin" +
                    "&destination=$destination" +
                    "&mode=driving" +
                    "&key=${getString(R.string.google_maps_api_key)}")

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


                        val colorResource = R.color.app_theme
                        val lineWidth = 20f // Larghezza della linea in pixel
                        parseDistanceFromJson(jsonResponse)
                        drawRouteOnMap(jsonResponse, colorResource, lineWidth)
                    } else {
                        // Gestisci qui gli errori di richiesta
                    }

                    disconnect()
                }
            }
        }
    }

    private fun parseDistanceFromJson(jsonResponse: String): String {
        val jsonObject = JSONObject(jsonResponse)
        val routesArray = jsonObject.getJSONArray("routes")

        if (routesArray.length() > 0) {
            val route = routesArray.getJSONObject(0)
            val legsArray = route.getJSONArray("legs")

            if (legsArray.length() > 0) {
                val leg = legsArray.getJSONObject(0)
                val distanceObject = leg.getJSONObject("distance")
                val distanceText = distanceObject.getString("text")

                return distanceText
            }
        }

        return "N/A" // Ritorna "N/A" se non è possibile estrarre la distanza
    }

    private fun drawRouteOnMap(jsonResponse: String, colorResource: Int, lineWidth: Float) {
        requireActivity().runOnUiThread {
            val jsonObject = JSONObject(jsonResponse)
            val routesArray = jsonObject.getJSONArray("routes")

            if (routesArray.length() > 0) {
                val route = routesArray.getJSONObject(0)
                val overviewPolyline = route.getJSONObject("overview_polyline")
                val points = overviewPolyline.getString("points")

                val decodedPath = PolyUtil.decode(points) // Assicurati di avere importato PolyUtil

                val color = ContextCompat.getColor(requireContext(), colorResource)

                val polylineOptions = PolylineOptions()
                    .addAll(decodedPath)
                    .color(color) // Imposta il colore della linea
                    .width(lineWidth) // Imposta la larghezza della linea in pixel
                    .geodesic(true)


                mMap.clear()

                val startLatLng = LatLng(decodedPath.first().latitude, decodedPath.first().longitude)
                val endLatLng = LatLng(decodedPath.last().latitude, decodedPath.last().longitude)


                mMap.addMarker(MarkerOptions()
                    .position(startLatLng)
                    .title("Partenza"))

                mMap.addMarker(MarkerOptions()
                    .position(endLatLng)
                    .title("Arrivo"))

                mMap.addPolyline(polylineOptions)

                // Ottieni i limiti del percorso
                val builder = LatLngBounds.Builder()
                for (point in decodedPath) {
                    builder.include(point)
                }
                val bounds = builder.build()

                // Imposta la posizione della telecamera in base ai limiti
                val padding = 100 // Margine intorno ai limiti del percorso
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                mMap.moveCamera(cameraUpdate)
            }
        }
    }



}
