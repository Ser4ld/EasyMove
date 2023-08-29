package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.adapter.MyAdapterRichieste
import com.example.easymove.databinding.FragmentRichiesteGuidatoreBinding


class RichiesteGuidatoreFragment : Fragment() {

    private var _binding: FragmentRichiesteGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var adapter: MyAdapterRichieste

    private lateinit var userId:String
    private lateinit var stato:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var argument=arguments
        if (argument != null ){
            stato= argument.getString("stato").toString()
        } else stato="Attesa"

        _binding = FragmentRichiesteGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("provastato2",stato)

        richiestaViewModel =
            ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerViewRichiesta.layoutManager = layoutManager

        userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {

                userViewModel.userDataLiveData.observe(viewLifecycleOwner) { user ->
                    if (user != null) {

                        userId = user.id
                        adapter = MyAdapterRichieste(ArrayList(), userData,richiestaViewModel)
                        binding.recyclerViewRichiesta.adapter = adapter
                    }
                }
            }

            richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiestaList ->
                if (richiestaList.isEmpty()) {
                    Log.i("provarichiesta", "No $richiestaList")
                } else {

                    var richiesteFiltrate = ArrayList(richiestaViewModel.filterRichiesteByUserIdAndStato(userId, stato, richiestaList))
                    adapter.updateRichieste(richiesteFiltrate)
                }
            }


        }
    }
}