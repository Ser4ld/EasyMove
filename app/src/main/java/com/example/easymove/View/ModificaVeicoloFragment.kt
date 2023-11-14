package com.example.easymove.View

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.MapViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.databinding.FragmentIndexBinding
import com.example.easymove.databinding.FragmentModificaEmailBinding
import com.example.easymove.databinding.FragmentModificaVeicoloBinding
import com.example.easymove.model.MapData
import com.example.easymove.model.Veicolo
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class ModificaVeicoloFragment : Fragment() {

    // Binding per manipolare gli oggetti nella schermata
    private var _binding:  FragmentModificaVeicoloBinding? = null
    private val binding get() = _binding!!

    private lateinit var targa: String
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var mapViewModel: MapViewModel

    private var positionData: MapData = MapData()
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    // Gestisce i risultati dell'attività di autocompletamento dell'indirizzo.
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)

                    // Ottieni i dettagli dell'indirizzo dal place e aggiorna la UI
                    mapViewModel.getAddressDetailsVeicolo(place, positionData)

                    binding.LocazioneVeicolo.text = Editable.Factory.getInstance().newEditable(positionData.address)
                    Log.i("ProvaOrigine", "$positionData")
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // L'utente ha annullato l'operazione di autocompletamento.
                Log.i("Prova", "User canceled autocomplete")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentModificaVeicoloBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza il viewmodel
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        // torna indietro
        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        Places.initialize(requireContext(), getString(R.string.map_api_key))

        // Definisce i campi che devono essere restituiti in seguito alla selezione
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


        // Controlla il focus del primo EditText
        binding.LocazioneVeicolo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                startAutocomplete.launch(intent) // Lancia l'autocomplete
                binding.LocazioneVeicolo.clearFocus() // Toglie il focus
                binding.LocazioneVeicolo.isCursorVisible = false // Toglie il cursore

            }
        }

        // Inizializza il ViewModel per la gestione dei veicoli e ottiene gli argomenti passati al fragment.
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        val argument = arguments

        // Verifica se ci sono argomenti passati al fragment e,
        // in caso positivo, ottiene la targa dal bundle.
        if(argument!= null){
            targa = argument.getString("targa").toString()
        }

        // Osserva i dati relativi ai veicoli e, quando sono disponibili, filtra il veicolo corrispondente alla targa fornita.
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner){veicoliList->
            if(veicoliList!= null){
                var veicolo = veicoliViewModel.filterListbyTarga(targa, veicoliList)
                if(veicolo!= null){

                    // Imposta i valori negli EditText del layout con i dati del veicolo
                    setEditText(veicolo)

                    // Gestisce il click sul pulsante di modifica veicolo richiamando la funzione
                    // checkModificaVeicolo che contolla che i nuovi parametri immessi siano corretti
                    binding.modificaVeicolo.setOnClickListener{
                        veicoliViewModel.checkModificaVeicolo(veicolo,imageUri, binding.LocazioneVeicolo.text.toString(), binding.TariffaKm.text.toString(), positionData){success, message->
                            if(success){
                                Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            }else{
                                Toast.makeText(requireContext(), message,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            }
        }

        // Gestisce il click sul pulsante per aprire il file chooser per l'immagine.
        binding.imageBtn.setOnClickListener {
            openFileChooser()
        }

    }

    // setta le edit text con i parametri del veicolo da modificare
    private fun setEditText(veicolo:Veicolo){
        binding.NomeVeicolo.setText(veicolo.modello)
        binding.LocazioneVeicolo.setText(veicolo.via)
        binding.Targa.setText(veicolo.targa)
        binding.TariffaKm.setText(veicolo.tariffakm)
        binding.capienzaCassone.setText(veicolo.capienza)

        if (!veicolo.imageUrl.isNullOrEmpty()) {
            // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
            Glide.with(requireContext())
                .load(veicolo.imageUrl)
                .into(binding.imageFirebase)
        } else {
            // Carica l'immagine di default
            binding.imageFirebase.setImageResource(R.drawable.baseline_image_24)
        }
    }

    // apre la schermata di selezione di un file
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/png" // Set the MIME type to restrict to PNG images
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // una volta selezionato il file, si controlla che questo sia del giusto formato (.PNG)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data!!

            // Verifying the MIME type of the selected file
            val contentResolver = requireContext().contentResolver
            val mime = contentResolver.getType(selectedImageUri)
            if (mime == "image/png") {
                // The selected file is a PNG image
                imageUri = selectedImageUri
                binding.imageFirebase.setImageURI(imageUri)
            } else {
                // The selected file is not a PNG image, handle the error
                Toast.makeText(requireContext(), "Please select a PNG image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}