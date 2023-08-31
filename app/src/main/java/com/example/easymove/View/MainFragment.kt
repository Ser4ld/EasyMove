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
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideKeyboard()

        homeViewModel = HomeViewModel()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        recensioneViewModel = ViewModelProvider(requireActivity()).get((RecensioneViewModel::class.java))
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        userViewModel.fetchUserData()
        userViewModel.fetchAllUser()
        veicoliViewModel.startVeicoliListener()
        recensioneViewModel.startRecensioniListener()
        richiestaViewModel.startRichiesteListener()
        richiestaViewModel.checkAndUpdateStato()

        //controllo se il frameLayout è vuoto
        if (childFragmentManager.findFragmentById(R.id.frameLayout) == null) {
            replaceFragment(HomeFragment())
            binding.bottomNavigationView.menu.findItem(R.id.homeItem).isChecked = true
        }



        //binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))
        binding.bottomNavigationView.background = null

        /*binding.addItem.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AggiungiVeicoloFragment())
                .addToBackStack(null)
                .commit()
        }
*/
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
                R.id.requestItem -> replaceFragment(RichiestePannelloGuidatoreFragment())
                R.id.addItem -> replaceFragment(AggiungiVeicoloFragment())
            }

           /* if(childFragmentManager.executePendingTransactions()){
                val currentFragment = childFragmentManager.findFragmentById(R.id.frameLayout)

                val isHomeOrProfileFragment = currentFragment is RichiestePannelloGuidatoreFragment || currentFragment is HomeFragment || currentFragment is ProfileFragment
                binding.bottomNavigationView.visibility = if (isHomeOrProfileFragment) View.VISIBLE else View.GONE

            }*/

            true
        }




        userViewModel.userDataLiveData.observe(viewLifecycleOwner) { currentUser ->

            if (currentUser != null) {
                binding.bottomNavigationView.menu.findItem(R.id.addItem).isVisible =
                    userViewModel.checkUserType(currentUser.userType)
            } else {
                // Se currentUser è null, nascondi l'elemento "Add Item"
                binding.bottomNavigationView.menu.findItem(R.id.addItem).isVisible = false
            }
        }

    }



    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


}

