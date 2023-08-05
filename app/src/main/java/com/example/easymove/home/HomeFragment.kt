import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class HomeFragment : Fragment(), OnMapReadyCallback {


    private var mGoogleMap: GoogleMap? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val mapFragment= childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inizializza il Places SDK
        Places.initialize(requireActivity().applicationContext, getString(R.string.google_map_api_key) )
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
            }

            override fun onError(status: Status) {
                Log.e("AutocompleteFragment", "Error in place selection: ${status.statusMessage}")
                Toast.makeText(requireContext(), "Errore in Cerca", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap=googleMap    }



}





