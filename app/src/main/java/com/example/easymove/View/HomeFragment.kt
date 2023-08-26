package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.TAG
import androidx.lifecycle.lifecycleScope
import com.example.easymove.R
import com.example.easymove.databinding.FragmentHomeBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mapbox.maps.logD
import com.mapbox.maps.plugin.logo.LogoView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class HomeFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap

    private var focusOriginBool: Boolean= false
    private var focusDestinationBool: Boolean= false


    private  var addressOrigin = ""
    private  var cityOrigin= ""
    private  var provinceOrigin= ""
    private  var regionOrigin= ""
    private  var countryOrigin= ""
    private  var postalCodeOrigin= ""
    private  var latitudeOrigin= ""
    private  var longitudeOrigin= ""

    private  var addressDestination = ""
    private  var cityDestination= ""
    private  var provinceDestination= ""
    private  var regionDestination= ""
    private  var countryDestination= ""
    private  var postalCodeDestination= ""
    private  var latitudeDestination= ""
    private  var longitudeDestination= ""

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    getAddressDetails(place)
                        var messaggio =
                            "Indirizzo: $addressOrigin, Città: $cityOrigin, CAP: $postalCodeOrigin, Provincia: $provinceOrigin, Regione: $regionOrigin, Nazione: $countryOrigin, Coordinate: ($latitudeOrigin,$longitudeOrigin) "
                        Log.i("Prova", messaggio)

                        var messaggio2 =
                            "Indirizzo: $addressDestination, Città: $cityDestination, CAP: $postalCodeDestination, Provincia: $provinceDestination, Regione: $regionDestination, Nazione: $countryDestination, Coordinate: ($latitudeDestination,$longitudeDestination) "
                        Log.i("Prova2", messaggio2)

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
            HttpRequestDirections()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Aggiunge il Sydney e sposta la camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun getAddressDetails(place: Place){

        val addressComponents = place.addressComponents.asList()

        if(focusOriginBool){
            addressOrigin=place.address

            binding.editTextOrigin.text=Editable.Factory.getInstance().newEditable(addressOrigin)

            latitudeOrigin=place.latLng.latitude.toString()
            longitudeOrigin=place.latLng.longitude.toString()

            for (component in addressComponents) {
                val types = component.types
                val name = component.name

                when {
                    "locality" in types -> cityOrigin = name
                    "administrative_area_level_2" in types -> provinceOrigin= name
                    "administrative_area_level_1" in types -> regionOrigin = name
                    "country" in types -> countryOrigin = name
                    "postal_code" in types -> postalCodeOrigin = name
                }

                focusOriginBool=false
            }


        } else if (focusDestinationBool) {

            addressDestination=place.address

            binding.editTextDestination.text=Editable.Factory.getInstance().newEditable(addressDestination)

            latitudeDestination=place.latLng.latitude.toString()
            longitudeDestination=place.latLng.longitude.toString()

            for (component in addressComponents) {
                val types = component.types
                val name = component.name

                when {
                    "locality" in types -> cityDestination = name
                    "administrative_area_level_2" in types -> provinceDestination= name
                    "administrative_area_level_1" in types -> regionDestination = name
                    "country" in types -> countryDestination = name
                    "postal_code" in types -> postalCodeDestination = name
                }
            }
            focusDestinationBool=false
        }
    }

    fun HttpRequestDirections() {
        lifecycleScope.launch {
            val origin = "$latitudeOrigin,$longitudeOrigin"
            val destination = "$latitudeDestination,$longitudeDestination"

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
                        // Ora puoi gestire la risposta JSON come desideri
                    } else {
                        // Gestisci qui gli errori di richiesta
                    }

                    disconnect()
                }
            }
        }
    }

}
