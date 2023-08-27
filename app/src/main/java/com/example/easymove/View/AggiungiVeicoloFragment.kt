package com.example.easymove.View

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.databinding.FragmentAggiungiVeicoloBinding
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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


class AggiungiVeicoloFragment : Fragment() {

    private var _binding: FragmentAggiungiVeicoloBinding? = null
    private val binding get() = _binding!!

    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var mapViewModel: MapViewModel

    private lateinit var userId: String
    private var positionData: MapData = MapData()

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    mapViewModel.getAddressDetailsVeicolo(place, positionData)
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
        _binding = FragmentAggiungiVeicoloBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        veicoliViewModel =  ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)


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


        // Inizializza l'SDK
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

        binding.imageBtn.setOnClickListener {
            openFileChooser()
        }

        binding.addVeicoloButton.setOnClickListener {
            // Verifica se sono stati inseriti tutti i dati necessari

            veicoliViewModel.storeVehicle(
                userId,
                binding.NomeVeicolo.text.toString(),
                binding.Targa.text.toString(),
                positionData.city,
                positionData.address,
                positionData.postalCode,
                binding.Altezzacassone.text.toString(),
                binding.Lunghezzacassone.text.toString(),
                binding.Larghezzacassone.text.toString(),
                binding.TariffaKm.text.toString(),
                imageUri,
                viewModelScope = viewLifecycleOwner.lifecycleScope
            ){success, message ->
                if(success){
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()

                }else{
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

            }

        }


    }


    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/png" // Set the MIME type to restrict to PNG images
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
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


