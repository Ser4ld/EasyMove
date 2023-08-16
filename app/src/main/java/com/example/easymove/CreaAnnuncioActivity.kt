package com.example.easymove

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.databinding.ActivityCreaAnnuncioBinding
import com.example.easymove.databinding.SignupBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

class CreaAnnuncioActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCreaAnnuncioBinding

    private lateinit var fireStoreDatabase: FirebaseFirestore

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    //private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private lateinit var coordinate: com.mapbox.geojson.Point
    private var streetMezzo: String? = null
    private var houseNumberMezzo: String? = null
    private var cityMezzo: String? = null
    private var regionMezzo: String? = null
    private var postcodeMezzo: String? = null
    private var fullAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreaAnnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreDatabase = FirebaseFirestore.getInstance()


        addressAutofill = AddressAutofill.create(getString(R.string.mapbox_access_token))
        var isFirstTyping = true

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

                if (isFirstTyping) {
                    Toast.makeText(applicationContext, "Formato: Via, Numero, Città", Toast.LENGTH_SHORT).show()
                    isFirstTyping = false
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


        binding.floatingActionButton.setOnClickListener{

            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.searchButton.setOnClickListener{

            //Metodo di gestione delle chiamate asincrone del dbms
            lifecycleScope.launch {
                val targaExists = checkTargaExists(binding.Targa.text.toString())

                if (targaExists) {
                    Toast.makeText(this@CreaAnnuncioActivity, "Targa già esistente", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (binding.NomeVeicolo.text.toString().isNotEmpty() &&
                    binding.Targa.text.toString().isNotEmpty() &&
                    binding.LocazioneVeicolo.text.toString().isNotEmpty() &&
                    binding.Lunghezzacassone.toString().isNotEmpty() &&
                    binding.Larghezzacassone.toString().isNotEmpty() &&
                    binding.Altezzacassone.toString().isNotEmpty()) {

                    val lunghezza = binding.Lunghezzacassone.text.toString().toDouble()
                    val altezza = binding.Altezzacassone.text.toString().toDouble()
                    val larghezza = binding.Larghezzacassone.text.toString().toDouble()
                    val capienza = calcoloCapienza(lunghezza, altezza, larghezza)

                    val indirizzoCompletoVerificato = fullAddress ?: ""
                    val viaVerificato = streetMezzo ?: ""
                    val numeroCivicoVerificato = houseNumberMezzo ?: ""
                    val cityVerificata = cityMezzo ?: ""
                    val regioneVerificata = regionMezzo ?: ""
                    val codicePostaleVerificato = postcodeMezzo ?: ""

                    val user = FirebaseAuth.getInstance().currentUser
                    val userEmail = user?.email

                    val hashMap = hashMapOf<String, Any>(
                        "Modello" to binding.NomeVeicolo.text.toString(),
                        "Targa" to binding.Targa.text.toString(),
                        "Capienza" to capienza,
                        "IndirizzoCompleto" to indirizzoCompletoVerificato,
                        "Via" to viaVerificato,
                        "NumeroCivico" to numeroCivicoVerificato,
                        "Città" to cityVerificata,
                        "Regione" to regioneVerificata,
                        "CodicePostale" to codicePostaleVerificato,
                        "Email" to userEmail.toString()
                    )

                    if (userEmail != null) {
                        val user = User(FirebaseFirestore.getInstance()) // RIVEDERE
                        user.uploadData(hashMap, "vans", binding.Targa.text.toString())
                    }

                    val intent = Intent(this@CreaAnnuncioActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@CreaAnnuncioActivity, "Errore", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun calcoloCapienza(lunghezza: Double, altezza: Double, larghezza: Double): String {
        val capienza = ((lunghezza * altezza * larghezza)/1000000)
        val formattedCapienza = String.format("%.2f", capienza)
        return "$formattedCapienza m³"
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
                Toast.makeText(this@CreaAnnuncioActivity, R.string.address_autofill_error_select, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddressAutofillResult(
        result: AddressAutofillResult,
        searchResults: SearchResultsView) {

        var address = result.address
        coordinate = result.suggestion.coordinate
        fullAddress = result.suggestion.formattedAddress

        streetMezzo = address.street
        houseNumberMezzo = address.houseNumber
        cityMezzo= address.place
        regionMezzo= address.region
        postcodeMezzo= address.postcode

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
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
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




}








