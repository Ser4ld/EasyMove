import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.easymove.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Trova la vista della mappa nel layout
        mapView = view.findViewById(R.id.mapView)

        // Inizializza la mappa
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Assegna il callback alla vista mappa
        mapView.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            googleMap = it

            // Personalizza la mappa come desideri
            // Esempio: Imposta il tipo di mappa
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Esegui altre operazioni sulla mappa
            // ...
            val position = LatLng(43.58685879237719, 13.516592134589143)
            it.addMarker(MarkerOptions().position(position).title("Marker"))
            it.moveCamera(CameraUpdateFactory.newLatLng(position))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
