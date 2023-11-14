package com.example.easymove.View

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.databinding.FragmentMainBinding
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel


class MainFragment : Fragment() {

    // Riferimento al binding per il layout del fragment
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Utilizza il binding per associare il layout del fragment al codice
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        // Restituisce la vista radice associata al layout del fragment
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nasconde la tastiera virtuale
        hideKeyboard()

        // Inizializza le ViewModel necessarie per il fragment
        homeViewModel = HomeViewModel()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        recensioneViewModel = ViewModelProvider(requireActivity()).get((RecensioneViewModel::class.java))
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        // Recupera i dati dell'utente e la lista di tutti gli utenti
        userViewModel.fetchUserData()
        userViewModel.fetchAllUser()

        // Avvia i listener utilizzato per rilevare gli aggiornamenti nei dati provenienti dal Firestore
        // per le ViewModel relative a veicoli, recensioni e richieste
        veicoliViewModel.startVeicoliListener()
        recensioneViewModel.startRecensioniListener()
        richiestaViewModel.startRichiesteListener()

        // Controlla e aggiorna lo stato delle richieste
        richiestaViewModel.checkAndUpdateStato()

        // Controllo se il FrameLayout è vuoto e sostituisce il fragment corrente con il fragment HomeFragment
        if (childFragmentManager.findFragmentById(R.id.frameLayout) == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigationView.menu.findItem(R.id.homeItem).isChecked = true
        }


        // Imposta il background della bottom navigation view a null per renderlo trasparente
        binding.bottomNavigationView.background = null


        // Gestisce la selezione degli elementi nella bottom navigation view
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->

            // fa visualizzare una schermata piuttosto che un'altra
            // in base all'id dell'elemento selezionato nella bottom bar
            when (menuItem.itemId) {
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
                R.id.requestItem -> replaceFragment(RichiestePannelloGuidatoreFragment())
                R.id.addItem -> replaceFragment(AggiungiVeicoloFragment())
            }

            true
        }



        // Osserva i dati dell'utente e mostra/nasconde l'elemento "Add Item" in base al tipo di utente
        userViewModel.userDataLiveData.observe(viewLifecycleOwner) { currentUser ->

            // Se l'utente è autenticato, mostra/nasconde l'elemento "Add Item" nella bottom bar
            // in base al tipo di utente tramite la funzione checkUserType
            if (currentUser != null) {
                binding.bottomNavigationView.menu.findItem(R.id.addItem).isVisible =
                    userViewModel.checkUserType(currentUser.userType)
            } else {
                // Se currentUser è null, nascondi l'elemento "Add Item"
                binding.bottomNavigationView.menu.findItem(R.id.addItem).isVisible = false
            }
        }

    }


    // Sostituisce il fragment corrente nel frameLayout con un nuovo fragment.
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Nasconde la tastiera virtuale.
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}
