package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.databinding.FragmentAggiungiVeicoloBinding
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.MapViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.model.MapData
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth


class AggiungiVeicoloFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentAggiungiVeicoloBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var mapViewModel: MapViewModel

    private lateinit var userId: String
    private var positionData: MapData = MapData()

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    // Dichiarazione di un Activity Result Launcher per l'autocompletamento della posizione del veicolo
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Se l'operazione di autocompletamento è stata completata con successo
                val intent = result.data
                if (intent != null) {

                    // Estrai il luogo selezionato dall'intento dell'autocompletamento
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    // Invoca un metodo nel view model per ottenere i dettagli dell'indirizzo del veicolo
                    mapViewModel.getAddressDetailsVeicolo(place, positionData)

                    // Imposta il testo nell'elemento UI corrispondente con l'indirizzo ottenuto
                    binding.LocazioneVeicolo.text = Editable.Factory.getInstance().newEditable(positionData.address)
                    Log.i("ProvaOrigine", "$positionData")
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i("Prova", "User canceled autocomplete")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentAggiungiVeicoloBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione dei ViewModel
        veicoliViewModel =  ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)


        // Configurazione del TextWatcher per convertire il testo in maiuscolo nel campo di input della targa
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
                    val upperCaseText = it.toString().uppercase()
                    if (it.toString() != upperCaseText) {
                        it.replace(0, it.length, upperCaseText)
                    }
                }
            }
        })

        // vengono osservati i dati dell'utente corrente per ottenere il suo id
        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                userId = userData.id
            }
        }


        // Inizializza l'SDK Places per l'autocompletamento
        Places.initialize(requireContext(), getString(R.string.map_api_key))

        // Definizione dei campi che devono essere restituiti dopo la selezione
        val fields = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG
        )

        // Avvia l'intent di autocompletamento
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setTypesFilter(listOf(PlaceTypes.ADDRESS))// Filtra i risultati per tipo
            .setCountries(listOf("IT")) // Filtra i risulati per città
            .build(requireContext())

        // Controlla il focus del primo EditText se questo ha il focus viene lanciato il meccanismo di autocompletamento
        binding.LocazioneVeicolo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startAutocomplete.launch(intent) // Lancia l'autocomplete
                binding.LocazioneVeicolo.clearFocus() // Toglie il focus
                binding.LocazioneVeicolo.isCursorVisible = false // Toglie il cursore

            }
        }

        // Configurazione del listener per aprire il selettore di file (per impostare l'immagine del van)
        binding.imageBtn.setOnClickListener {
            openFileChooser()
        }

        // Configurazione del listener per aggiungere un veicolo
        binding.addVeicoloButton.setOnClickListener {

            // Verifica se sono stati inseriti tutti i dati necessari e memorizza
            // il van tramite il metodo storeVehicle
            veicoliViewModel.storeVehicle(
                userId,
                binding.NomeVeicolo.text.toString(),
                binding.Targa.text.toString(),
                positionData.city,
                positionData.address,
                positionData.postalCode,
                positionData.latitude,
                positionData.longitude,
                binding.Altezzacassone.text.toString(),
                binding.Lunghezzacassone.text.toString(),
                binding.Larghezzacassone.text.toString(),
                binding.TariffaKm.text.toString(),
                imageUri,
                viewModelScope = viewLifecycleOwner.lifecycleScope
            ){success, message ->
                if(success){
                    // Se la memorizzazione del veicolo va a buon fine fa comparire il dialog di conferma,
                    // pulisce il form e nascode la tastiera
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    hideKeyboard()
                    dialog()
                    clearForm()


                }else{
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            }

        }

    }


    // Funzione per ripulire il form dopo l'aggiunta del veicolo
    private fun clearForm(){
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_image_24)
        binding.Targa.text.clear()
        binding.LocazioneVeicolo.text.clear()
        binding.NomeVeicolo.text.clear()
        binding.Altezzacassone.text.clear()
        binding.Larghezzacassone.text.clear()
        binding.Lunghezzacassone.text.clear()
        binding.TariffaKm.text.clear()
        binding.imageFirebase.setImageDrawable(vectorDrawable)
    }


    // Funzione per mostrare un dialog personalizzato
    private fun dialog() {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_layout_dialog2, null)
        builder.setView(customView)
        val dialog = builder.create()

        //imposta lo sfodo del dialog a trasparente per poter applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        // Imposta il listener per il pulsante OK nel layout del dialog
        customView.findViewById<Button>(R.id.btnOK).setOnClickListener{
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()

    }

    // Funzione per aprire il selettore di file
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/png" // Set the MIME type to restrict to PNG images
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Gestione del risultato dell'attività di selezione del file
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data!!

            // Verifica del MIME type del file selezionato
            val contentResolver = requireContext().contentResolver
            val mime = contentResolver.getType(selectedImageUri)
            if (mime == "image/png") {
                // Il file selezionato è un'immagine PNG
                imageUri = selectedImageUri
                binding.imageFirebase.setImageURI(imageUri)
            } else {
                // Il file selezionato non è un'immagine PNG restituisci un messaggio di errore
                Toast.makeText(requireContext(), "Please select a PNG image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Funzione per nascondere la tastiera
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}


