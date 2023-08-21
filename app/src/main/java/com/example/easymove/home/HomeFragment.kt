package com.example.easymove.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.R
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.annunci.AnnunciActivity
import com.example.easymove.databinding.FragmentHomeBinding
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

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel


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

    private var streetOrigin: String? = null
    private var houseNumberOrigin: String? = null
    private var cityOrigin: String? = null
    private var regionOrigin: String? = null
    private var postcodeOrigin: String? = null

    private var streetDestination: String? = null
    private var houseNumberDestination: String? = null
    private var cityDestination: String? = null
    private var regionDestination: String? = null
    private var postcodeDestination: String? = null

    private val POLO_MONTEDAGO = Point.fromLngLat(13.516539114888866, 43.586912987628324)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        addressAutofill2 = AddressAutofill.create(getString(R.string.mapbox_access_token))

        mapboxMap = binding.map.getMapboxMap()

        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)



        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        //binding.searchResultsView = view.findViewById(R.id.search_results_view)
        //searchResultsView2 = view.findViewById(R.id.search_results_view2)
        var isFirstTyping1 = true
        var isFirstTyping2 = true


        binding.searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        binding.searchResultsView2.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        addressAutofillUiAdapter = AddressAutofillUiAdapter(
            view = binding.searchResultsView,
            addressAutofill = addressAutofill
        )

        addressAutofillUiAdapter2 = AddressAutofillUiAdapter(
            view = binding.searchResultsView2,
            addressAutofill = addressAutofill2
        )


        mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(POLO_MONTEDAGO)
                .zoom(9.0)
                .build()
        )


        addressAutofillUiAdapter.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(
                    suggestion,
                    fromReverseGeocoding = false,
                    binding.queryText,
                    addressAutofill,
                    binding.searchResultsView,
                    //binding.fullAddress
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
                    binding.queryText2,
                    addressAutofill2,
                    binding.searchResultsView2
                    //binding.fullAddress2
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
       binding.queryText.setOnEditorActionListener { _, actionId, _ ->
           homeViewModel.checkActionId(actionId)
        }



        binding.queryText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (isFirstTyping1) {
                    Toast.makeText(requireContext(), "Formato: Via, Numero, Città", Toast.LENGTH_SHORT).show()
                    isFirstTyping1 = false
                }
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
                binding.searchResultsView.isVisible = query != null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })

        binding.queryText2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {

                if (isFirstTyping2) {
                    Toast.makeText(requireContext(), "Formato: Via, Numero, Città", Toast.LENGTH_SHORT).show()
                    isFirstTyping2 = false
                }

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
                binding.searchResultsView2.isVisible = query != null
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
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
        }

        binding.searchButton.setOnClickListener() {
            val intentSearch = Intent(requireContext(), AnnunciActivity::class.java)
            // Pass the city of departure to the next activity
            intentSearch.putExtra("cityOrigin", cityOrigin)
            startActivity(intentSearch)
        }

    }

    private fun selectSuggestion(
        suggestion: AddressAutofillSuggestion,
        fromReverseGeocoding: Boolean,
        editText: EditText,
        Autofill: AddressAutofill,
        searchResults: SearchResultsView
        //, textView: TextView

    ) {
        lifecycleScope.launchWhenStarted {
            val response = Autofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result, fromReverseGeocoding,editText,searchResults)
            }.onError {
                Toast.makeText(requireContext(), R.string.address_autofill_error_select, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult,
        fromReverseGeocoding: Boolean,
        editText: EditText,
        searchResults: SearchResultsView) {

        var address = result.address
        coordinate = result.suggestion.coordinate

        if(editText==binding.queryText){
            origin=coordinate
            streetOrigin = address.street
            houseNumberOrigin = address.houseNumber
            cityOrigin= address.place
            regionOrigin= address.region
            postcodeOrigin= address.postcode


            editText.setText(
                listOfNotNull(
                    streetOrigin,
                    houseNumberOrigin,
                    cityOrigin
                ).joinToString()
            );
        }
        if(editText==binding.queryText2){
            destination=coordinate
            streetDestination = address.street
            houseNumberDestination = address.houseNumber
            cityDestination= address.place
            regionDestination= address.region
            postcodeDestination= address.postcode



            editText.setText(
                listOfNotNull(
                    streetDestination,
                    houseNumberDestination,
                    cityDestination
                ).joinToString()
            );
        }

        if (!fromReverseGeocoding) {
            binding.map.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(coordinate)
                    .zoom(16.0)
                    .build()
            )
            ignoreNextMapIdleEvent = true
            addMarkerToMap(coordinate)

        }

        ignoreNextQueryTextUpdate = true

        editText.clearFocus()

        searchResults.isVisible = false
        searchResults.hideKeyboard()


        binding.provadistanza.text = homeViewModel.getDistancePoints()

    }
    private companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 0
    }

    private fun View.hideKeyboard() {
        context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun addMarkerToMap(point: Point) {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        homeViewModel.bitmapFromDrawableRes(requireContext(), R.drawable.red_marker)?.let {
            val annotationApi = binding.map?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(binding.map!!)
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

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
}
