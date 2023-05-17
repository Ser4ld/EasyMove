import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

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
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Assegna il callback alla vista mappa
        mapView.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Personalizza la mappa come desideri
        // Esempio: Imposta il tipo di mappa
        googleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        // Esegui altre operazioni sulla mappa
        // ...

        val startPoint = LatLng(43.58685879237719, 13.516592134589143)
        val endPoint = LatLng(42.86820613903017, 13.921166498128718)
        googleMap?.addMarker(MarkerOptions().position(startPoint).title("Punto di partenza"))
        googleMap?.addMarker(MarkerOptions().position(endPoint).title("Punto di arrivo"))

        // Centra la mappa sulla posizione desiderata
        val cameraPosition = CameraPosition.Builder()
            .target(startPoint) // Posizione desiderata
            .zoom(12f) // Livello di zoom desiderato
            .build()

        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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
