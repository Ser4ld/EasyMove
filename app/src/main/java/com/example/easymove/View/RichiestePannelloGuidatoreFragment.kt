package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentRichiesteGuidatoreBinding
import com.example.easymove.databinding.FragmentRichiestePannelloGuidatoreBinding
import com.google.android.material.tabs.TabLayout

class RichiestePannelloGuidatoreFragment : Fragment() {


    private var _binding: FragmentRichiestePannelloGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRichiestePannelloGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel =
            ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        userViewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {

                richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiesteList ->
                    if (richiesteList != null) {

                        var richiesteTotali=richiestaViewModel.filterRichiesteByUserId(user.id,richiesteList)
                        binding.textRichiesteTotali2.text=richiestaViewModel.totaleRichieste(richiesteTotali).toString()

                        var richiesteInAttesa=richiestaViewModel.filterRichiesteByUserIdAndStato(user.id,"Attesa",richiesteList)
                        binding.textRichiesteAttesa2.text=richiestaViewModel.totaleRichieste(richiesteInAttesa).toString()

                        var richiesteAccettate=richiestaViewModel.filterRichiesteByUserIdAndStato(user.id,"Accettata",richiesteList)
                        binding.textRichiesteAccettate2.text=richiestaViewModel.totaleRichieste(richiesteAccettate).toString()

                    }
                }
            }


            binding.fabButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            if (childFragmentManager.findFragmentById(R.id.frameLayoutRichieste) == null) {
                val initialFragment = RichiesteGuidatoreFragment()
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutRichieste, initialFragment)
                    .commit()
            }


            val attesaTab = binding.tabLayout.newTab().setText("IN ATTESA")
            val accettateTab = binding.tabLayout.newTab().setText("ACCETTATE")
            val terminateTab = binding.tabLayout.newTab().setText("TERMINATE")

            binding.tabLayout.addTab(attesaTab)
            binding.tabLayout.addTab(accettateTab)
            binding.tabLayout.addTab(terminateTab)

            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    var stato: String = when (tab.position) {
                        0 -> "Attesa"
                        1 -> "Accettata"
                        2 -> "Altro"
                        else -> throw IllegalArgumentException("Invalid tab position")
                    }

                    val bundle = Bundle()
                    bundle.putString("stato", stato)

                    val richiesteGuidatoreFragment = RichiesteGuidatoreFragment()
                    richiesteGuidatoreFragment.arguments = bundle

                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutRichieste, richiesteGuidatoreFragment)
                        .commit()


                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })


        }
    }
}