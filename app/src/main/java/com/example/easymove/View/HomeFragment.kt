package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                            "Indirizzo: ${originData.address}, Città: ${originData.city}, CAP: ${originData.postalCode}, Provincia: ${originData.province}, Regione: ${originData.region}, Nazione: ${originData.country}, Coordinate: (${originData.latitude},${originData.longitude}) "
                        Log.i("Prova", messaggio)

                        var messaggio2 =
                            "Indirizzo: ${destinationData.address}, Città: ${destinationData.city}, CAP: ${destinationData.postalCode}, Provincia: ${destinationData.province}, Regione: ${destinationData.region}, Nazione: ${destinationData.country}, Coordinate: (${destinationData.latitude},${destinationData.longitude}) "
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

    fun HttpRequestDirections() {
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
