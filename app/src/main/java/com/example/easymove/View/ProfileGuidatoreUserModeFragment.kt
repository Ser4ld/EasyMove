package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentProfileGuidatoreUserModeBinding
import com.example.easymove.model.Recensione
import com.example.easymove.model.User

class ProfileGuidatoreUserModeFragment : Fragment() {

    // Variabile per il binding del fragment
    private var _binding: FragmentProfileGuidatoreUserModeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Utilizzato per creare l'oggetto di binding associato al layout del fragment
        _binding = FragmentProfileGuidatoreUserModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializza i ViewModel necessari e gestisce il comportamento del pulsante di navigazione indietro.
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)

        // Torna indietro all'ultima schermata visualizzata
        binding.floatingActionButton3.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        /**
         * Osserva le modifiche nella lista di recensioni e nella lista di utenti.
         * Carica i dati dell'utente e aggiorna l'interfaccia utente in base ai dati disponibili.
         */
        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioneList ->
            userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->
                if (userList != null) {

                    // prende gli argomenti passati dal fragment passato
                    val argument = arguments

                    if (argument != null) {
                        // Memorizza l'id del guidatore
                        val guidatoreId = argument.getString("idguidatore").toString()

                        //Filtra una lista di utenti per trovare l'utente con l'ID specificato.
                        user = userViewModel.FilterListById(guidatoreId, userList)!!

                        // crea il bundle e va ad inserire al suo interno l'id del guidatore
                        val bundle = Bundle()
                        bundle.putString("idguidatore", user.id)

                        if (user != null) {
                            // Carica i dati dell'utente e aggiorna l'interfaccia utente
                            uploadUserData(user, recensioneList)

                            // Configura i pulsanti per visualizzare le recensioni
                            binding.buttonVisualizzaRecensioni.setOnClickListener {
                                val recensioniGuidatorePublicFragment = RecensioniGuidatorePublicFragment()
                                recensioniGuidatorePublicFragment.arguments = bundle
                                replaceFragment(recensioniGuidatorePublicFragment)
                            }

                            // Configura i pulsanti per visualizzare i veicoli dell'utente
                            binding.buttonVisualizzaVeicoli.setOnClickListener {
                                val veicoliGuidatorePublicFragment = VeicoliGuidatorePublicFragment()
                                veicoliGuidatorePublicFragment.arguments = bundle
                                replaceFragment(veicoliGuidatorePublicFragment)
                            }
                        }
                    }
                }
            }
        }
    }

    // Sostituisce il frammento corrente con un nuovo frammento.
    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    //Carica i dati dell'utente nell'interfaccia utente.
    private fun uploadUserData(user: User, recensioneList: List<Recensione>) {

        // Aggiorna i campi dell'interfaccia utente con i dati dell'utente e delle recensioni
        binding.textEmail2.text = user.email
        binding.textView3.text = "${user.name} ${user.surname}"
        binding.ratingBarMedia.rating = recensioneViewModel.mediaRecensioniFiltrate(user.id, recensioneList, user.userType)

        // Carica l'immagine dell'utente utilizzando la libreria Glide
        if (!user.imageUrl.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(user.imageUrl)
                .circleCrop()
                .into(binding.imageView)
        } else {

            // Se l'utente non ha un'immagine, carica un'immagine di default
            binding.imageView.setImageResource(R.drawable.baseline_image_24)
        }
    }
}
