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


class HomeFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mMap: GoogleMap

    private  var address = ""
    private  var city= ""
    private  var province= ""
    private  var region= ""
    private  var country= ""
    private  var postalCode= ""
    private  var latitude= ""
    private  var longitude= ""

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    Log.i(
                        "ciao", "Place: ${place.name}, ${place.id}"
                    )
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("ciao", "User canceled autocomplete")
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
        val fields = listOf( Place.Field.NAME, Place.Field.ADDRESS,  Place.Field.ADDRESS_COMPONENTS,Place.Field.LAT_LNG,)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireContext())

        binding.editTextOrigin.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startAutocomplete.launch(intent)
            }
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


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

        address=place.address
        latitude=place.latLng.latitude.toString()
        longitude=place.latLng.longitude.toString()

        val addressComponents = place.addressComponents.asList()

        for (component in addressComponents) {
            val types = component.types
            val name = component.name

            when {
                "locality" in types -> city = name
                "administrative_area_level_2" in types -> province = name
                "administrative_area_level_1" in types -> region = name
                "country" in types -> country = name
                "postal_code" in types -> postalCode = name
            }
        }

    }


}
