package com.example.easymove.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.adapter.MyAdapterVeicoli
import com.example.easymove.databinding.FragmentVeicoliGuidatorePublicBinding
import com.example.easymove.model.User

class VeicoliGuidatorePublicFragment : Fragment() {

    private var _binding: FragmentVeicoliGuidatorePublicBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var veicoliViewModel: VeicoliViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel
    private lateinit var user: User
    private lateinit var adapter: MyAdapterVeicoli

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVeicoliGuidatorePublicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        veicoliViewModel = ViewModelProvider(requireActivity()).get(VeicoliViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        binding.floatingActionButton2.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->
            userViewModel.userDataLiveData.observe(viewLifecycleOwner) { userData ->
                if (userData != null && userList != null) {
                    val argument = arguments
                    if (argument != null) {
                        val guidatoreId = argument.getString("idguidatore").toString()
                        user = userViewModel.FilterListById(guidatoreId, userList)!!

                        if (user != null) {
                            adapter = MyAdapterVeicoli(
                                veicoliViewModel,
                                userViewModel,
                                richiestaViewModel,
                                ArrayList(),
                                emptyList(),
                                "",
                                userList
                            )
                            binding.recyclerView.adapter = adapter

                            veicoliViewModel.veicoliLiveData.observe(viewLifecycleOwner) { veicoliList ->
                                val recensioniFiltrate =
                                    veicoliViewModel.filterVeicoliByUserId(guidatoreId, veicoliList)

                                if (recensioniFiltrate.isEmpty()) {
                                    binding.emptyLayout.visibility = View.VISIBLE
                                } else {
                                    binding.emptyLayout.visibility = View.GONE
                                }

                                adapter.updateData(recensioniFiltrate)
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
