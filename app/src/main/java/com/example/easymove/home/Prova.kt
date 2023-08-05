package com.example.easymove.home
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easymove.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class prova : AppCompatActivity(), OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.provamappa)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inizializza l'API di Places
        Places.initialize(applicationContext, getString(R.string.google_map_api_key))

        // Configura l'AutocompleteSupportFragment
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        )

        // Gestisci i risultati dell'autocompletamento
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Toast.makeText(this@prova, "YES", Toast.LENGTH_SHORT).show()
            }

            override fun onError(status: Status) {
                val errorMessage = "Errore in Cerca: ${status.statusMessage}"
                Toast.makeText(this@prova, errorMessage, Toast.LENGTH_SHORT).show()
                Log.e("AutocompleteError", errorMessage) // Aggiungi questo log per visualizzare l'errore nel logcat
            }

        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
    }
}
