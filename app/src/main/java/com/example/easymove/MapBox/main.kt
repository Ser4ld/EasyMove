package com.example.easymove.MapBox

import android.Manifest
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.easymove.R
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillResult
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import java.util.Locale
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
class main: AppCompatActivity() {

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofill2: AddressAutofill


    private lateinit var searchResultsView: SearchResultsView
    private lateinit var searchResultsView2: SearchResultsView

    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    private lateinit var addressAutofillUiAdapter2: AddressAutofillUiAdapter


    private lateinit var queryEditText: EditText
    private lateinit var fullAddress: TextView
    private lateinit var pinCorrectionNote: TextView
    private lateinit var mapView: MapView
    private lateinit var mapPin: View
    private lateinit var mapboxMap: MapboxMap
    private lateinit var textViewDistanza: TextView

    private lateinit var queryEditText2: EditText
    private lateinit var fullAddress2: TextView
    private lateinit var mapPin2: View
    private lateinit var textViewDistanza2: TextView
    private lateinit var coordinate2: Point

    private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private lateinit var coordinate: Point

    private lateinit var origin: Point
    private lateinit var destination: Point

    var streetOrigin:String? = null
    var houseNumberOrigin:String? = null
    var cityOrigin:String?= null
    var regionOrigin:String?= null
    var postcodeOrigin:String?= null

    var streetDestination:String? = null
    var houseNumberDestination:String? = null
    var cityDestination:String?= null
    var regionDestination:String?= null
    var postcodeDestination:String?= null


    private val TOWER_BRIDGE = Point.fromLngLat(-0.07515, 51.50551)
    private val POLO_MONTEDAGO = Point.fromLngLat(13.516539114888866, 43.586912987628324)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boxmap)

        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        addressAutofill2 = AddressAutofill.create(getString(R.string.mapbox_access_token))

        queryEditText = findViewById(R.id.query_text)
        fullAddress = findViewById(R.id.full_address)
        textViewDistanza = findViewById(R.id.provadistanza)

        queryEditText2 = findViewById(R.id.query_text2)
        fullAddress2 = findViewById(R.id.full_address2)

        pinCorrectionNote = findViewById(R.id.pin_correction_note)
        mapView = findViewById(R.id.map)
        mapboxMap = mapView.getMapboxMap()

        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)


        /** mapboxMap.addOnMapIdleListener {
        if (ignoreNextMapIdleEvent) {
        ignoreNextMapIdleEvent = false
        return@addOnMapIdleListener
        }

        val mapCenter = mapboxMap.cameraState.center
        findAddress(mapCenter)
        }*/

        searchResultsView = findViewById(R.id.search_results_view)
        searchResultsView2 = findViewById(R.id.search_results_view2)


        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        searchResultsView2.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        addressAutofillUiAdapter = AddressAutofillUiAdapter(
            view = searchResultsView,
            addressAutofill = addressAutofill
        )

        addressAutofillUiAdapter2 = AddressAutofillUiAdapter(
            view = searchResultsView2,
            addressAutofill = addressAutofill2
        )

        LocationEngineProvider.getBestLocationEngine(applicationContext)
            .lastKnownLocation(this) { point ->
                point?.let {
                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(9.0)
                            .build()
                    )
                    ignoreNextMapIdleEvent = true
                }
            }

        addressAutofillUiAdapter.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(
                    suggestion,
                    fromReverseGeocoding = false,
                    queryEditText,
                    addressAutofill,
                    searchResultsView,
                    fullAddress
                )
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })

        addressAutofillUiAdapter2.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(
                    suggestion,
                    fromReverseGeocoding = false,
                    queryEditText2,
                    addressAutofill2,
                    searchResultsView2,
                    fullAddress2
                )
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })

        //listener che impedisce di scendere nella queryEditText2 se si preme invio sulla tastiera
        queryEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                // Consume the event, preventing the default action
                true
            } else {
                false
            }
        }


        queryEditText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryTextUpdate) {
                    ignoreNextQueryTextUpdate = false
                    return
                }

                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launchWhenStarted {
                        addressAutofillUiAdapter.search(query)
                    }
                }
                searchResultsView.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })

        queryEditText2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryTextUpdate) {
                    ignoreNextQueryTextUpdate = false
                    return
                }

                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launchWhenStarted {
                        addressAutofillUiAdapter2.search(query)
                    }
                }
                searchResultsView2.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
        }

    }

    /**private fun findAddress(point: Point) {
    // Avvia una coroutine quando l'Activity è nello stato "started"
    lifecycleScope.launchWhenStarted {
    // Chiede all'API di autocompletamento di restituire suggerimenti per l'indirizzo alla coordinata geografica specificata
    val response = addressAutofill.suggestions(point, AddressAutofillOptions())

    // Gestisce il risultato positivo della chiamata
    response.onValue { suggestions ->
    // Se non ci sono suggerimenti, mostra un messaggio di toast per chiedere all'utente di inserire un indirizzo
    if (suggestions.isEmpty()) {
    showToast("Inserisci un indirizzo")
    } else {
    // Se ci sono suggerimenti, seleziona il primo suggerimento e visualizza i dettagli dell'indirizzo
    selectSuggestion(
    suggestions.first(),
    fromReverseGeocoding = true
    )
    }
    }
    // Gestisce eventuali errori durante la chiamata
    .onError {
    // Mostra un messaggio di toast per segnalare un errore di correzione del pin
    showToast(R.string.address_autofill_error_pin_correction)
    }
    }
    }*/


    private fun selectSuggestion(
        suggestion: AddressAutofillSuggestion,
        fromReverseGeocoding: Boolean,
        editText: EditText,
        Autofill: AddressAutofill,
        searchResults: SearchResultsView,
        textView: TextView

    ) {
        lifecycleScope.launchWhenStarted {
            val response = Autofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result, fromReverseGeocoding,editText,searchResults,textView)
            }.onError {
                showToast(R.string.address_autofill_error_select)
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult,
        fromReverseGeocoding: Boolean,
        editText: EditText,
        searchResults: SearchResultsView,
        textView: TextView) {

        var address = result.address


        coordinate = result.suggestion.coordinate

        if(textView==queryEditText){
            streetOrigin = address.street
            houseNumberOrigin = address.houseNumber
            cityOrigin= address.place
            regionOrigin= address.region
            postcodeOrigin= address.postcode

            editText.setText(
                listOfNotNull(
                    "$streetOrigin $houseNumberOrigin",
                    cityOrigin
                ).joinToString(", ")
            );
        }
        if(textView==queryEditText2){
            streetDestination = address.street
            houseNumberDestination = address.houseNumber
            cityDestination= address.place
            regionDestination= address.region
            postcodeDestination= address.postcode

            editText.setText(
                listOfNotNull(
                    "$streetDestination $houseNumberDestination",
                    cityDestination
                ).joinToString(", ")
            );
        }
        textView.isVisible = true
        textView.text = result.suggestion.formattedAddress

        pinCorrectionNote.isVisible = true

        if (!fromReverseGeocoding) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(coordinate)
                    .zoom(16.0)
                    .build()
            )
            ignoreNextMapIdleEvent = true
            //mapPin.isVisible = true
            addMarkerToMap(coordinate)
        }

        ignoreNextQueryTextUpdate = true


        editText.clearFocus()

        searchResults.isVisible = false
        searchResults.hideKeyboard()

        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(applicationContext, cityOrigin+cityDestination, duration)
        toast.show()

    }

    private companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 0
    }

    private fun addMarkerToMap(point: Point) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this@main,
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(point)
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun getDistancePoints(){
        if(streetOrigin?.isNotEmpty()==true  && streetDestination?.isNotEmpty()==true ){
            var distanza = TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_KILOMETERS)
            var distanzaFormattata = String.format(Locale.getDefault(), "%.2f", distanza)
            var distanzaStringa = "$distanzaFormattata chilometri dal polo montedago"


        }
    }
}
