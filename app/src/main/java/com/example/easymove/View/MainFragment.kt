package com.example.easymove.View

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.CreaAnnuncioActivity
import com.example.easymove.R
import com.example.easymove.ViewModel.HomeViewModel
import com.example.easymove.databinding.FragmentIndexBinding
import com.example.easymove.databinding.FragmentMainBinding
import com.example.easymove.home.HomeFragment
import com.example.easymove.profilo.ProfileFragment
import com.example.easymove.repository.UserRepository


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = HomeViewModel()

        //controllo se il frameLayout è vuoto
        if (childFragmentManager.findFragmentById(R.id.frameLayout) == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomAppBar.setBackgroundColor(resources.getColor(R.color.white))
        binding.bottomNavigationView.background = null

        binding.addItem.setOnClickListener {
            val intent = Intent(requireContext(), CreaAnnuncioActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeItem -> replaceFragment(HomeFragment())
                R.id.profileItem -> replaceFragment(ProfileFragment())
            }
            true
        }

        // Qui chiamiamo la funzione per ottenere il valore di "tipoutente"
        homeViewModel.fetchAndSetTipoutente { isGuidatore ->
            if (isGuidatore) {
                binding.addItem.visibility = View.VISIBLE
            } else {
                binding.addItem.visibility = View.GONE
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    }

