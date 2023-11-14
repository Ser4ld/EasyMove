package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.ViewModel.MapViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() , OnMapReadyCallback {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mapViewModel: MapViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel

    // Variabili per la gestione della mappa
    private lateinit var mMap: GoogleMap
    private var isMapReady = false
    private var focusOriginBool: Boolean = false

    // Dati sulla mappa per l'origine e la destinazione
    private var originData: MapData = MapData()
    private var destinationData: MapData = MapData()

    // Dati sulla mappa per l'origine e la destinazione
    private lateinit var distance: String
    private lateinit var timeTravel: String

    // Variabile per memorizzare lo stato di ricerca
    private var searchOkBool = false

    // Registra un risultato per l'attività di avvio per la selezione dell'indirizzo
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {

                    // Ottieni il luogo selezionato dall'intent
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    // Ottieni i dettagli dell'indirizzo utilizzando il metodo getAddressDetails del MapViewModel
                    mapViewModel.getAddressDetails(
                        place,
                        originData,
                        destinationData,
                        focusOriginBool
                    )

                    if (focusOriginBool) {
                        // Aggiorna la edit text dell'origine con l'indirizzo selezionato
                        binding.editTextOrigin.text =
                            Editable.Factory.getInstance().newEditable(originData.address)
                    } else {
                        // Aggiorna la edit text della destinazione con l'indirizzo selezionato
                        binding.editTextDestination.text =
                            Editable.Factory.getInstance().newEditable(destinationData.address)
                    }

                    Log.i("ProvaOrigine", "$originData")
                    Log.i("ProvaDestinazione", "$destinationData")


                    // Verifica la validità dei dati inseriti dall'utente
                    homeViewModel.checkFormEditTexts(
                        originData,
                        destinationData,
                        binding.editTextOrigin,
                        binding.editTextDestination
                    ) { isValid, errorMessage ->
                        if (isValid) {

                            // Esegue la richiesta HTTP per le indicazioni e gestisce la risposta JSON
                            mapViewModel.HttpRequestDirections(
                                viewModelScope = viewLifecycleOwner.lifecycleScope,
                                originData,
                                destinationData,
                                getString(R.string.map_api_key)
                            ) { JSONResponse ->

                                // Esegue il parsing della distanza e del tempo di viaggio dalla risposta JSON
                                val (parsedDistance, parsedTimeTravel) = mapViewModel.parseDistanceTimeFromJson(JSONResponse)

                                // Aggiorna le variabili di distanza e tempo di viaggio
                                distance = parsedDistance
                                timeTravel = parsedTimeTravel

                                // Aggiorna le edit text della distanza e del tempo
                                binding.textDistance2.text=distance
                                binding.textTime2.text=timeTravel

                                Log.d("prova distanza", distance)
                                Log.d("prova tempo", timeTravel)

                                // Disegna la rotta sulla mappa
                                drawRouteOnMap(JSONResponse)

                                searchOkBool=true
                            }

                        } else {
                            if (errorMessage != null) {
                                Log.e("Errore", errorMessage)
                            }
                        }
                    }
                }
            }
            // L'utente ha annullato l'operazione di selezione dell'indirizzo
            else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.i("Prova", "User canceled autocomplete")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza i ViewModel necessari
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get((VeicoliViewModel::class.java))

        // Inizializza l'SDK di Google Places utilizzando la chiave API
        Places.initialize(requireContext(), getString(R.string.map_api_key))

        // Definisce i campi che devono essere restituiti in seguito alla selezione di un indirizzo
        val fields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG
        )

        // Crea un intent per l'auto-completamento degli indirizzi
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypesFilter(listOf(PlaceTypes.ADDRESS))// Filtra i risultati per tipo
            .setCountries(listOf("IT")) // Filtra i risulati per città
            .build(requireContext())

        // Controlla il focus del primo EditText (Origine)
        binding.editTextOrigin.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusOriginBool = true
                startAutocomplete.launch(intent) // Lancia l'autocomplete
                binding.editTextOrigin.clearFocus() // Toglie il focus
                binding.editTextOrigin.isCursorVisible = false // Toglie il cursore

            }
        }

        // Controlla il focus del secondo EditText (Destinazione)
        binding.editTextDestination.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                focusOriginBool = false
                startAutocomplete.launch(intent) // Lancia l'autocomplete
                binding.editTextDestination.clearFocus() // Toglie il focus
                binding.editTextDestination.isCursorVisible = false // Toglie il cursore

            }
        }

        // Ottiene il fragment della mappa e registra il callback quando la mappa è pronta
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)


        // Evento legato al click del bottone buttonSearch per ricercare un veicolo
        // dopo aver impostato gli indirizzi origine e arrivo
        binding.buttonSearch.setOnClickListener {

            // Verifica la correttezza degli indirizzi di partenza e destinazione
            homeViewModel.checkFormEditTexts(
                    originData,
                    destinationData,
                    binding.editTextOrigin,
                    binding.editTextDestination
                ) { isValid, errorMessage ->
                    if (isValid) {
                        if(searchOkBool){

                        // Crea un bundle con i dati necessari e imposta il fragment della ListaVeicoliFragment
                        val listaVeicoliFragment = ListaVeicoliFragment()
                        val bundle = Bundle()

                        Log.i("origine1", "$originData")
                        bundle.putString("originCity", originData.city)
                        bundle.putString("originPostCode", originData.postalCode)
                        bundle.putString("origin",originData.address)
                        bundle.putString("destination",destinationData.address)
                        bundle.putString("distance", distance)

                        listaVeicoliFragment.arguments = bundle

                        // Sostituisce il fragment corrente con ListaVeicoliFragment
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, listaVeicoliFragment)
                            .addToBackStack(null)
                            .commit()
                        searchOkBool=false
                        }
                    }
                    else {
                        if (errorMessage != null) {
                            Toast.makeText(requireContext(), errorMessage , Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        }


        // Verifica il tipo di utente e nasconde il pulsante di ricerca se questo è un guidatore
        userViewModel.userDataLiveData.observe(viewLifecycleOwner){user->
            if(user!= null){
                if(userViewModel.checkUserType(user.userType)){
                    binding.buttonSearch.visibility = GONE
                }
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {

        mMap=googleMap

        // Pulisce gli EditText degli indirizzi
        binding.editTextOrigin.setText("")
        binding.editTextDestination.setText("");

        try {
                    // Osserva i veicoli dal ViewModel e aggiunge i marker sulla mappa
                    veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
                    mMap.clear()

                    //ottiene le cordinate di ciascun veicolo nella lista
                    val driverVehiclesCoordinates = veicoliViewModel.getCoordinatesForDriverVehicles(veicoliList)

                    for (coordinatePair in driverVehiclesCoordinates) {
                        // Aggiunge il marker del veicolo sulla mappa
                        val latLng = LatLng(coordinatePair.first.toDouble(), coordinatePair.second.toDouble())
                        googleMap.addMarker(MarkerOptions().position(latLng).title("Veicolo"))
                    }
                }
            } catch (e: Exception) {
                Log.e("MapError", "Errore durante l'aggiunta dei marker sulla mappa: ${e.message}")
            }

            // Imposta la posizione e il livello di zoom iniziali della mappa
            val italyLatLng = LatLng(41.8719, 12.5674)
            val zoomLevel = 5.5f
            val italyBounds = LatLngBounds(
                LatLng(36.5, 7.0),   // Latitudine minima e longitudine minima
                LatLng(46.5, 17.0)   // Latitudine massima e longitudine massima
            )


            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(italyLatLng, zoomLevel))
            mMap.setLatLngBoundsForCameraTarget(italyBounds)

            // Imposta il livello di zoom minimo e massimo
            val minZoomLevel = 5.5f

            mMap.setOnCameraMoveListener {
                val currentZoom = mMap.cameraPosition.zoom
                if (currentZoom < minZoomLevel) {
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(minZoomLevel))
                }
            }
    }


    // Funzione che disegna il percorso sulla mappa utilizzando la risposta JSON
    private fun drawRouteOnMap(jsonResponse: String) {
        //Si esegue il parsing del file JSON
        val decodedPath = mapViewModel.ParseRoute(jsonResponse)

        requireActivity().runOnUiThread {

            val colorResource = R.color.app_theme
            val lineWidth = 20f

            // se il percorso non è vuoto si va a settarlo sulla mappa
            if (decodedPath.isNotEmpty()) {

                val color = ContextCompat.getColor(requireContext(), colorResource)

                val polylineOptions = PolylineOptions()
                    .addAll(decodedPath)
                    .color(color) // Imposta il colore della linea
                    .width(lineWidth) // Imposta la larghezza della linea in pixel
                    .geodesic(true)


                mMap.clear()

                val startLatLng = LatLng(
                    originData.latitude.toString().toDouble(),
                    originData.longitude.toString().toDouble()
                )
                val endLatLng = LatLng(
                    destinationData.latitude.toString().toDouble(),
                    destinationData.longitude.toString().toDouble()
                )


                // Aggiunge i marker di partenza e arrivo
                mMap.addMarker(
                    MarkerOptions()
                        .position(startLatLng)
                        .title("Partenza")
                )

                mMap.addMarker(
                    MarkerOptions()
                        .position(endLatLng)
                        .title("Arrivo")
                )

                // Aggiunge la polyline sulla mappa
                mMap.addPolyline(polylineOptions)

                // Ottieni i limiti del percorso
                val builder = LatLngBounds.Builder()
                for (point in decodedPath) {
                    builder.include(point)
                }
                val bounds = builder.build()

                // Imposta la posizione della telecamera in base ai limiti
                val padding = 130 // Margine intorno ai limiti del percorso
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                mMap.moveCamera(cameraUpdate)
            }
        }
    }

}

