package com.example.easymove.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRecensioni
import com.example.easymove.databinding.FragmentRecensioniGuidatoreBinding

class RecensioniGuidatoreFragment : Fragment() {

    private var _binding: FragmentRecensioniGuidatoreBinding? = null
    private val binding get() = _binding!!

    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var adapter: MyAdapterRecensioni

    private lateinit var userId:String
    private lateinit var userType: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecensioniGuidatoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        userViewModel=  ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                userViewModel.userDataLiveData.observe(viewLifecycleOwner){ user->
                    if( user != null ) {
                        userType= user.userType

                        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner){ recensioniList->
                            if (recensioniList!=null) {
                                binding.ratingBarMedia.rating= recensioneViewModel.mediaRecensioniFiltrate(user.id,recensioniList,userType)
                                binding.textRecensioniTotali2.text= recensioneViewModel.totaleRecensioniFiltrate(user.id,recensioniList,userType).toString()
                            }
                        }
                        userId=user.id

                        adapter = MyAdapterRecensioni(ArrayList(), userData, user.userType)
                        binding.recyclerView.adapter = adapter

                    }
                }
            }

        }

        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioniList ->
            if (recensioniList.isEmpty()) {
                binding.emptyLayout.visibility= View.VISIBLE
            } else {
                var recensioniFiltrate= recensioneViewModel.filterRecensioneByUserId(userId, recensioniList, userType)

                if (recensioniFiltrate.isEmpty()) {
                    binding.emptyLayout.visibility= View.VISIBLE
                } else {
                    binding.emptyLayout.visibility= View.GONE
                }

                adapter.updateRecensioni(recensioniFiltrate)
            }
        }
    }


}