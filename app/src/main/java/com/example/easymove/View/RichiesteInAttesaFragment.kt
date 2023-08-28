package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.adapter.MyAdapterRichieste
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentRichiesteInAttesaBinding


class RichiesteInAttesaFragment : Fragment() {

    private var _binding: FragmentRichiesteInAttesaBinding? = null
    private val binding get() = _binding!!

    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var userViewModel: UserViewModel


    private lateinit var adapter: MyAdapterRichieste

    private lateinit var userId:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRichiesteInAttesaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        adapter = MyAdapterRichieste(ArrayList(), userData)
                        binding.recyclerViewRichiesta.adapter = adapter
                    }
                }
            }

            richiestaViewModel.richiesteLiveData.observe(viewLifecycleOwner) { richiestaList ->
                if (richiestaList.isEmpty()) {
                    Log.i("provarichiesta", "No $richiestaList")
                } else {
                    adapter.updateRichieste(ArrayList(richiestaList))
                }
            }


        }
    }
}