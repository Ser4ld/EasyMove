package com.example.easymove.View

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebStorage.Origin
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.easymove.BuildConfig.MAPS_API_KEY
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.R
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.databinding.FragmentHomeBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillResult
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import kotlin.properties.Delegates

class HomeFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding

/*    private lateinit var homeViewModel: HomeViewModel

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofill2: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    private lateinit var addressAutofillUiAdapter2: AddressAutofillUiAdapter

    private lateinit var mapboxMap: MapboxMap

    private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private lateinit var coordinate: Point
    private var origin: Point = Point.fromLngLat(0.0, 0.0)
    private var destination : Point = Point.fromLngLat(0.0, 0.0)

    private lateinit var streetOrigin: String
    private lateinit var houseNumberOrigin: String
    private lateinit var cityOrigin: String
    private lateinit var regionOrigin: String
    private lateinit var postcodeOrigin: String
    private lateinit var adressOrigin: String

    private lateinit var streetDestination: String
    private lateinit var houseNumberDestination: String
    private lateinit var cityDestination: String
    private lateinit var regionDestination: String
    private lateinit var postcodeDestination: String
    private lateinit var addressDestination: String

    private val POLO_MONTEDAGO = Point.fromLngLat(13.516539114888866, 43.586912987628324)

    private var longitudine: Double = 0.0
    private var latitudine: Double = 0.0*/


    private lateinit var mMap: GoogleMap



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


        // Initialize the SDK
        Places.initialize(requireContext(), "AIzaSyBy1Ck8fhL9cRuXdz_2DvZmiTV3FnNu0WQ")

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(requireContext())



        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS))


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Toast.makeText(
                    requireContext(),
                    "Place: ${place.name} + ${place.address}+  ${place.addressComponents} ",
                    Toast.LENGTH_SHORT
                ).show()

                val selectedPlaceLatLng = place.latLng

                if (selectedPlaceLatLng != null) {
                    // Rimuovi eventuali marker esistenti
                    mMap.clear()

                    // Aggiungi un nuovo marker con le coordinate del luogo selezionato
                    mMap.addMarker(
                        MarkerOptions()
                            .position(selectedPlaceLatLng)
                            .title(place.name)
                    )

                    // Sposta la telecamera sulla posizione del nuovo marker
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceLatLng, 12f))

                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Toast.makeText(requireContext(), "An error occurred: $status", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }




}
