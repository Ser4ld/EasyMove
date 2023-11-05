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
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.adapter.MyAdapterRecensioni
import com.example.easymove.databinding.FragmentRecensioniGuidatoreBinding
import com.example.easymove.databinding.FragmentRecensioniGuidatorePublicBinding
import com.example.easymove.model.User


class RecensioniGuidatorePublicFragment : Fragment() {
    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var userViewModel: UserViewModel
    private var _binding: FragmentRecensioniGuidatorePublicBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyAdapterRecensioni
    private lateinit var user: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecensioniGuidatorePublicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recensioneViewModel = ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)
        userViewModel=  ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager


        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioneList ->
            userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->
                userViewModel.userDataLiveData.observe(viewLifecycleOwner){userData->
                    if(userData!=null) {
                        if (userList != null) {
                            val argument = arguments
                            if (argument != null) {
                                val guidatoreId = argument.getString("idguidatore").toString()
                                user = userViewModel.FilterListById(guidatoreId, userList)!!
                                if (user != null) {
                                    adapter = MyAdapterRecensioni(ArrayList(), userList,"guidatore")
                                    binding.recyclerView.adapter = adapter
                                    if (recensioneList.isEmpty()) {
                                        binding.emptyLayout.visibility = View.VISIBLE
                                    } else {

                                        var recensioniFiltrate = recensioneViewModel.filterRecensioneByUserId(
                                            user.id,
                                            recensioneList,
                                            user.userType )

                                        if (recensioniFiltrate.isEmpty()) {
                                            binding.emptyLayout.visibility = View.VISIBLE
                                        } else {
                                            binding.emptyLayout.visibility = View.GONE
                                        }

                                        adapter.updateRecensioni(recensioniFiltrate)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}