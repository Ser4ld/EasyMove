package com.example.easymove.View

import android.os.Bundle
import android.util.Log
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
import com.example.easymove.model.Richiesta
import com.google.android.material.tabs.TabLayout

class RichiestePannelloGuidatoreFragment : Fragment() {


    private var _binding: FragmentRichiestePannelloGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel

    private var stato: String = "Attesa"



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRichiestePannelloGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = Bundle()
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        var richiesteTotali: List<Richiesta>
        var richiesteInAttesa: List<Richiesta>
        var richiesteAccettate: List<Richiesta>
        var richiesteCompletate: List<Richiesta>



        userViewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {

                richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiesteList ->
                    if (richiesteList != null) {

                        richiesteTotali = richiestaViewModel.filterRichiesteByUserId(
                            user.id,
                            richiesteList,
                            user.userType
                        )
                        richiesteInAttesa = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Attesa",
                            richiesteList,
                            user.userType
                        )
                        richiesteAccettate = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Accettata",
                            richiesteList,
                            user.userType
                        )

                        richiesteCompletate = richiestaViewModel.filterRichiesteByUserIdAndStato(
                            user.id,
                            "Completata",
                            richiesteList,
                            user.userType
                        )

                        binding.textRichiesteTotali2.text =
                            richiestaViewModel.totaleRichieste(richiesteTotali).toString()
                        binding.textRichiesteAttesa2.text =
                            richiestaViewModel.totaleRichieste(richiesteInAttesa).toString()
                        binding.textRichiesteAccettate2.text =
                            richiestaViewModel.totaleRichieste(richiesteAccettate).toString()
                        binding.textRichiesteCompletate2.text= richiestaViewModel.totaleRichieste(richiesteCompletate).toString()
                    }
                }
            }
        }

            /*binding.fabButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }*/

            if (childFragmentManager.findFragmentById(R.id.frameLayoutRichieste) == null) {
                val initialFragment = RichiesteGuidatoreFragment()
                childFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutRichieste, initialFragment)
                    .commit()
            }


            val attesaTab = binding.tabLayout.newTab().setText("IN ATTESA")
            val accettateTab = binding.tabLayout.newTab().setText("ACCETTATE")
            val completateTab = binding.tabLayout.newTab().setText("COMPLETATE")
            val terminateTab = binding.tabLayout.newTab().setText("RIFIUTATE")


            binding.tabLayout.addTab(attesaTab)
            binding.tabLayout.addTab(accettateTab)
            binding.tabLayout.addTab(completateTab)
            binding.tabLayout.addTab(terminateTab)


  /*      if (selectedTabPosition >= 0 && selectedTabPosition < binding.tabLayout.tabCount) {
            binding.tabLayout.getTabAt(selectedTabPosition)?.select()
        }
        Log.i("provastato","$stato")*/




            binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {


                    stato = when (tab.position) {
                        0 -> "Attesa"
                        1 -> "Accettata"
                        2 -> "Completata"
                        3 -> "Rifiutata"
                        else -> throw IllegalArgumentException("Posizione Tab non valida")
                    }


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