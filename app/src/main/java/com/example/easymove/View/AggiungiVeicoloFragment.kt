package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.R
import com.example.easymove.databinding.FragmentAggiungiVeicoloBinding
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillResult
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.properties.Delegates
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel


class AggiungiVeicoloFragment : Fragment() {
    private var _binding: FragmentAggiungiVeicoloBinding? = null
    private val binding get() = _binding!!
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var userId: String

    private lateinit var fireStoreDatabase: FirebaseFirestore

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    //private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private lateinit var coordinate: com.mapbox.geojson.Point

    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var streetMezzo: String
    private lateinit var houseNumberMezzo: String
    private lateinit var cityMezzo: String
    private lateinit var regionMezzo: String
    private lateinit var postcodeMezzo: String
    private lateinit var fullAddress: String

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAggiungiVeicoloBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        veicoliViewModel = VeicoliViewModel()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        //caratteri editText uppercase
        binding.Targa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Non è necessario implementare nulla qui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Non è necessario implementare nulla qui
            }

            override fun afterTextChanged(s: Editable?) {
                // Converte il testo in maiuscolo
                //s?.let se s != null viene eseguito il codice tra le parentesi
                s?.let {
                    val upperCaseText = it.toString().toUpperCase()
                    if (it.toString() != upperCaseText) {
                        //it.replace(startIndex, endIndex, testodasostituire), startIndex indice da cui iniziare a sostituire, endIndex indice fino al quale sostituire
                        it.replace(0, it.length, upperCaseText)
                    }
                }
            }
        })

        binding.floatingActionButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                userId = userData.id
            }
        }

        binding.imageBtn.setOnClickListener {
            openFileChooser()
        }

        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        //var isFirstTyping = true da eliminare

        binding.searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        addressAutofillUiAdapter = AddressAutofillUiAdapter(
            view = binding.searchResultsView,
            addressAutofill = addressAutofill
        )

        addressAutofillUiAdapter.addSearchListener(object :
            AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(
                    suggestion,
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

        binding.LocazioneVeicolo.addTextChangedListener(object : TextWatcher {

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
                binding.searchResultsView.isVisible = query != null
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



        binding.addVeicoloButton.setOnClickListener {
            // Verifica se sono stati inseriti tutti i dati necessari


            veicoliViewModel.storeVehicle(
                userId,
                binding.NomeVeicolo.text.toString(),
                binding.Targa.text.toString(),
                "chieti",
                "via dei tintori",
                "2",
                "66100",
                binding.Altezzacassone.text.toString(),
                binding.Lunghezzacassone.text.toString(),
                binding.Larghezzacassone.text.toString(),
                binding.TariffaKm.text.toString(),
                imageUri,
                viewModelScope = viewLifecycleOwner.lifecycleScope
            ){success, message ->
                if(success){
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                //no success
                }

            }

        }

    }



    private fun selectSuggestion(
        suggestion: AddressAutofillSuggestion,
        Autofill: AddressAutofill,
        searchResults: SearchResultsView

    ) {
        lifecycleScope.launchWhenStarted {
            val response = Autofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result,searchResults)
            }.onError {
                Toast.makeText(requireContext(), R.string.address_autofill_error_select, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult,
        searchResults: SearchResultsView) {

        var address = result.address

        coordinate = result.suggestion.coordinate
        latitude=coordinate.latitude()
        longitude=coordinate.longitude()

        fullAddress = result.suggestion.formattedAddress

        streetMezzo = address.street.toString()
        houseNumberMezzo = address.houseNumber.toString()
        cityMezzo= address.place.toString()
        regionMezzo= address.region.toString()
        postcodeMezzo= address.postcode.toString()

        binding.LocazioneVeicolo.setText(
            listOfNotNull(
                streetMezzo,
                houseNumberMezzo,
                cityMezzo
            ).joinToString()
        );


        ignoreNextQueryTextUpdate = true

        binding.LocazioneVeicolo.clearFocus()

        searchResults.isVisible = false
        searchResults.hideKeyboard()

    }
    private companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 0
    }
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }
    private fun View.hideKeyboard() {
        context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }


    //Funzione utilizzata per controllare tramite una query se esiste un altro veicolo con la stessa targa
    // appena immessa

    private suspend fun checkTargaExists(targa: String): Boolean {
        val snapshot = fireStoreDatabase.collection("vans")
            .whereEqualTo("Targa", targa)
            .get()
            .await()

        return !snapshot.isEmpty
    }


    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!

            // Verifica l'estensione del file
            val contentResolver = contentResolver
            val mime = contentResolver.getType(selectedImageUri)
            if (mime != null && mime == "image/png") {
                // Il file selezionato è un PNG
                imageUri = selectedImageUri
                binding.imageFirebase.setImageURI(imageUri)
                binding.disclaimerFormato.visibility = View.GONE

            } else {
                // Il file selezionato non è un PNG
                Toast.makeText(requireContext(), "Seleziona un'immagine in formato PNG", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    //versione senza vincolo sull'immagine png in ingresso
      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)

          if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
              imageUri = data.data!!
              binding.imageFirebase.setImageURI(imageUri)
          }
      }
}


