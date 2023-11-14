package com.example.easymove.View

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.databinding.FragmentInoltraRichiestaBinding
import java.text.SimpleDateFormat
import java.util.*

class InoltraRichiestaFragment : Fragment() {

    // Binding per manipolare gli oggetti nella schermata
    private var _binding: FragmentInoltraRichiestaBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel

    private lateinit var modello: String
    private lateinit var tariffaKm: String
    private lateinit var distance: String
    private lateinit var targa: String
    private lateinit var capienza: String
    private lateinit var idGuidatore: String
    private lateinit var destination: String
    private lateinit var origin: String
    private lateinit var userId: String
    private lateinit var prezzo: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentInoltraRichiestaBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione viewmodel che serviranno nella view
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)

        // Evento di click che ti porta alla schermata precedente
        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Evento di click che fa visualizzare il calendario per la scelta della data dell'appuntamento
        binding.textData.setOnClickListener {
            showCalendario()
        }

        //Viene osservato il live data dei veicoli
        veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner){ veicoliList->

            // Verifica se la targa è presente nella lista dei veicoli disponibili.
            var inoltroBool = veicoliViewModel.verificaTargaPresente(targa,veicoliList)

            if(inoltroBool){
                // Se la targa è presente, imposta il click listener per il pulsante di invio richiesta.
                binding.richiestabtn.setOnClickListener{
                    // Verifica se sono stati inseriti tutti i dati necessari
                    richiestaViewModel.inoltraRichiesta(
                        idGuidatore,
                        userId,
                        targa,
                        origin,
                        destination,
                        prezzo,
                        binding.textData.text.toString(),
                        binding.textDescription.text.toString(),
                    ){success, message ->
                        if(success){
                            // Se la richiesta ha avuto successo, mostra un messaggio di conferma e torna alla schermata precedente
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                            parentFragmentManager.popBackStack()

                        }else{
                            // Se c'è un errore durante l'inoltro della richiesta, mostra un messaggio di errore.
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            //no success
                        }

                    }

                }
            } else {
                // Se la targa non è presente, mostra un messaggio indicando che il veicolo non è disponibile e
                // torna alla schermata precedente.
                Toast.makeText(requireContext(), "Veicolo non disponibile", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }



        }

        // Ottiene i dati passati dal precedente fragment, attraverso gli argomenti e
        // imposta i testi nelle viste corrispondenti
        val argument = arguments
        if (argument != null) {
            // setta i dati passati attraverso gli argomenti del fragment
            distance=argument.getString("distance").toString()
            tariffaKm = argument.getString("tariffaKm").toString()
            modello = argument.getString("modello").toString()
            targa = argument.getString("targa").toString()
            capienza = argument.getString("capienza").toString()
            idGuidatore = argument.getString("id_guidatore").toString()
            destination = argument.getString("destination").toString()
            origin =  argument.getString("origin").toString()

            // imposta il testo riguardante il modello e la destinazione nella vista
            binding.textViewVeicolo2.text = modello
            binding.textViewDestination2.text = destination

            // Calcola il prezzo in base alla distanza e alla tariffa per chilometro
            prezzo= richiestaViewModel.calcolaPrezzo(distance,tariffaKm).toString()

        }

        // Osserva la lista di tutti gli utenti per ottenere il nome e il cognome del guidatore
        userViewModel.allUsersLiveData.observe(
            viewLifecycleOwner,
            ) { userList ->
            val foundUser = userList.firstOrNull { user -> user.id == idGuidatore }
            if(foundUser!= null){
                binding.textViewGuidatore2.text= "${foundUser.name} ${foundUser.surname}"
            }
        }

        // Osserva i dati dell'utente corrente per ottenere l'ID utente
        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
               userId= userData.id
            }
        }

    }

    /**
     * Mostra un DatePickerDialog per la selezione della data.
     * Imposta la data minima selezionabile come il giorno corrente.
     */
    private fun showCalendario() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                binding.textData.setText(formattedDate)
            },
            year,
            month,
            day
        )

        // Imposta la data minima selezionabile come il giorno corrente
        val minDate = calendar.timeInMillis
        datePickerDialog.datePicker.minDate = minDate

        datePickerDialog.show()
    }

}
