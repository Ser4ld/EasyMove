package com.example.easymove

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

class CreaAnnuncioActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCreaAnnuncioBinding

    private lateinit var fireStoreDatabase: FirebaseFirestore

    private lateinit var addressAutofill: AddressAutofill
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
    //private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    private lateinit var coordinate: com.mapbox.geojson.Point

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var streetMezzo: String? = null
    private var houseNumberMezzo: String? = null
    private var cityMezzo: String? = null
    private var regionMezzo: String? = null
    private var postcodeMezzo: String? = null
    private var fullAddress: String? = null


    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreaAnnuncioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStoreDatabase = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("uploads")

        binding.imageBtn.setOnClickListener {
            openFileChooser()
        }

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

        binding.searchButton.setOnClickListener {
            // Verifica se sono stati inseriti tutti i dati necessari
            if (binding.NomeVeicolo.text.toString().isEmpty() ||
                binding.Targa.text.toString().isEmpty() ||
                //binding.LocazioneVeicolo.text.toString().isEmpty() ||
                binding.Lunghezzacassone.text.toString().isEmpty() ||
                binding.Larghezzacassone.text.toString().isEmpty() ||
                binding.Altezzacassone.text.toString().isEmpty() ||
                binding.TariffaKm.text.toString().isEmpty()) {
                Toast.makeText(this@CreaAnnuncioActivity, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica se è stata selezionata un'immagine
            if (imageUri == null) {
                Toast.makeText(this@CreaAnnuncioActivity, "Seleziona un'immagine prima di creare l'annuncio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica se la targa esiste già
            val targa = binding.Targa.text.toString()
            lifecycleScope.launch {
                val targaExists = checkTargaExists(targa)

                if (targaExists) {
                    Toast.makeText(this@CreaAnnuncioActivity, "Targa già esistente", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Esegue il caricamento dell'immagine su Firebase Storage
                val storageRef = FirebaseStorage.getInstance().reference.child("images")
                val imageName = "${System.currentTimeMillis()}.png"
                val imageRef = storageRef.child(imageName)

                imageRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Caricamento completato con successo
                        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val imageUrl = downloadUri.toString()
                            // Continua con il processo di creazione dell'annuncio e salva l'URL dell'immagine nel database
                            saveAnnouncementWithImage(imageUrl)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Errore durante il caricamento dell'immagine
                        Toast.makeText(this@CreaAnnuncioActivity, "${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun saveAnnouncementWithImage(imageUrl: String) {
        val lunghezza = binding.Lunghezzacassone.text.toString().toDouble()
        val altezza = binding.Altezzacassone.text.toString().toDouble()
        val larghezza = binding.Larghezzacassone.text.toString().toDouble()
        val capienza = calcoloCapienza(lunghezza, altezza, larghezza)

        // Altri dati dell'annuncio
        val modello = binding.NomeVeicolo.text.toString()
        val targa = binding.Targa.text.toString()
        val tariffaKm = binding.TariffaKm.text.toString()
        /*val indirizzoCompleto = fullAddress ?: ""
        val via = streetMezzo ?: ""
        val numeroCivico = houseNumberMezzo ?: ""
        val citta = cityMezzo ?: ""
        val regione = regionMezzo ?: ""
        val codicePostale = postcodeMezzo ?: ""*/

        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email

        val hashMap = hashMapOf<String, Any>(
            "modello" to modello,
            "targa" to targa,
            "capienza" to capienza,
            "tariffaKm" to tariffaKm,
            /*"indirizzoCompleto" to indirizzoCompleto,
            "via" to via,
            "numeroCivico" to numeroCivico,
            "citta" to citta,
            "regione" to regione,
            "codicePostale" to codicePostale,*/
            "email" to userEmail.toString(),
           /* "latitudine" to latitude.toString(),
            "longitudine" to longitude.toString(),*/
            "imageUrl" to imageUrl // Aggiungi l'URL dell'immagine
        )

        if (userEmail != null) {
            val user = User(FirebaseFirestore.getInstance()) // RIVEDERE
            user.uploadData(hashMap, "vans", targa)
        }

        val intent = Intent(this@CreaAnnuncioActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
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
        latitude=coordinate.latitude()
        longitude=coordinate.longitude()

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

    private val PICK_IMAGE_REQUEST = 1

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                Toast.makeText(this, "Seleziona un'immagine in formato PNG", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //versione senza vincolo sull'immagine png in ingresso
  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imageFirebase.setImageURI(imageUri)
        }
    }*/





}








