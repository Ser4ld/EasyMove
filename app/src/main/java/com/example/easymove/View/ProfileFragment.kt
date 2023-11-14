package com.example.easymove.View

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.easymove.View.ModificaEmailFragment
import com.example.easymove.View.ResetPasswordFragment
import com.example.easymove.View.IndexActivity

class ProfileFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    // codice di richiesta dell'immagine dal selettore di file
    private val PICK_IMAGE_REQUEST = 1
    // URI dell'immagine selezionata, verrà utilizzato per l'aggiornamento dell'immagine del profilo
    private var imageUri: Uri? = null
    private lateinit var userId:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza il ViewModel dell'utente utilizzando il ViewModelProvider
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        // Osserva i dati dell'utente dal ViewModel
        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->

            // Controlla se i dati dell'utente sono non nulli
            if (userData != null) {

                // Aggiorna le text con i dati dell'utente
                binding.nomeTV.text = userData.name
                binding.cognomeTV.text = userData.surname
                binding.emailTV.text = userData.email
                binding.benvenutoTV.text = "Benvenuto " + userData.name

                // Ottieni l'ID dell'utente corrente
                userId= userData.id


                // Carica l'immagine del profilo se presente
                if(userData.imageUrl != ""){
                    Glide.with(requireContext())
                        .load(userData.imageUrl)
                        .circleCrop() // Rende l'immagine circolare
                        .into(binding.imageUser)
                }

                // Sostituisci il fragment nel frameLayoutProfile in base al tipo di utente, se è guidatore lo sostituisce
                // con il ProfileGuidatoreFragment se è consumatore lo sostituisce con il ProfileConsumatoreFragment
                if (userData.userType == "guidatore" ){
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutProfile, ProfileGuidatoreFragment())
                        .commit()
                }else{
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutProfile, ProfileConsumatoreFragment())
                        .commit()
                }
            }
        }

        // All'evento di click del bottone modificaPasswordbtn si passa alla schermata di modificapassword
        binding.modificaPasswordbtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit()
        }

        // al click del bottone di logout si esegue il logout dell'utente
        binding.logout.setOnClickListener {
            logout()
        }

        // All'evento di click del bottone modificabtn si passa alla schermata di Modifica Email
        binding.modificabtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ModificaEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        // All'evento di click del bottone-icona floatingButtonUpdateImage si richiama il metodo openFileChooser in
        // grado di aprire un selettore di immagini per scegliere l'immagine profilo
        binding.floatingButtonUpdateImage.setOnClickListener {
            openFileChooser()
        }


    }

    private fun logout() {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        // Imposta lo sfondo del dialog a trasparente per applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Imposta il listener per il pulsante "Yes" nel dialog che se premuto esegue il logout utilizzando Firebase Auth
        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout effettuato", Toast.LENGTH_SHORT).show()

            // Avvia l'activity di login dopo il logout
            val intentLogout = Intent(requireContext(), IndexActivity::class.java)
            startActivity(intentLogout)

            // Chiudi l'activity corrente
            requireActivity().finish()

            // Chiudi il dialog dopo il logout
            dialog.dismiss()
        }

        // Se viene premuto il pulsante no sul popup lo fa chiudere e non sucede nulla
        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()


    }


    // Apre un selettore di file per consentire all'utente di selezionare un'immagine dal dispositivo
    private fun openFileChooser() {
        // Crea un intent per ottenere il contenuto
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        // Imposta il MIME type per limitare la selezione alle immagini PNG
        intent.type = "image/png"

        // apre la chermata di selezione file per selezionare l'immagine di profilo
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    // avviamo l'onActivityResult per ottenere il risultato della schermata di selezione file
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verifica se il risultato proviene dalla richiesta di selezione dell'immagine
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            // Ottieni l'URI dell'immagine selezionata
            val selectedImageUri = data.data!!

            // Verifica il MIME type dell'immagine selezionata
            val contentResolver = requireContext().contentResolver
            val mime = contentResolver.getType(selectedImageUri)

            // Se il MIME type è "image/png", procedi con l'aggiornamento dell'immagine
            if (mime == "image/png") {

                // Salva l'URI dell'immagine e chiama il metodo updateImageUrl per aggiornare l'URL dell'immagine utente
                imageUri = selectedImageUri
                userViewModel.updateImageUrl(userId, imageUri!!) { success, errMsg ->

                    // Mostra un messaggio Toast in base al successo o al fallimento dell'operazione
                    if (success) {
                        Toast.makeText(requireContext(), "Immagine aggiornata con successo", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Errore durante l'aggiornamento dell'immagine", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Se il MIME type non è "image/png", mostra un messaggio di errore
                Toast.makeText(requireContext(), "Please select a PNG image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}