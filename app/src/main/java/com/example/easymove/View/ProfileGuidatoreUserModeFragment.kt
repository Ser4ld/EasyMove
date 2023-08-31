package com.example.easymove.View

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.databinding.FragmentProfileConsumatoreBinding
import com.example.easymove.databinding.FragmentProfileGuidatoreUserModeBinding
import com.example.easymove.model.Recensione
import com.example.easymove.model.User


class ProfileGuidatoreUserModeFragment : Fragment() {

    private var _binding: FragmentProfileGuidatoreUserModeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var recensioneViewModel: RecensioneViewModel
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileGuidatoreUserModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        recensioneViewModel =
            ViewModelProvider(requireActivity()).get(RecensioneViewModel::class.java)

        binding.floatingActionButton3.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recensioneViewModel.recensioniLiveData.observe(viewLifecycleOwner) { recensioneList ->
            userViewModel.allUsersLiveData.observe(viewLifecycleOwner) { userList ->
                if (userList != null) {
                    val argument = arguments
                    if (argument != null) {
                        val guidatoreId = argument.getString("idguidatore").toString()
                        user = userViewModel.FilterListById(guidatoreId, userList)!!
                        if(user != null){
                            uploadUserData(user, recensioneList)
                            binding.buttonVisualizzaRecensioni.setOnClickListener{
                                Log.d("provaaaa234556", user.userType)
                                var bundle = Bundle()
                                bundle.putString("idguidatore", user.id)
                                val recensioniGuidatorePublicFragment = RecensioniGuidatorePublicFragment()
                                recensioniGuidatorePublicFragment.arguments = bundle
                                replaceFragment(recensioniGuidatorePublicFragment)
                            }
                        }
                    }
                }
            }
        }


    }
    private fun replaceFragment(fragment : Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

    }
    private fun uploadUserData(user: User, recensioneList: List<Recensione>){
        binding.textEmail2.text = user.email
        binding.textView3.text="${user.name} ${user.surname}"
        binding.ratingBarMedia.rating = recensioneViewModel.mediaRecensioniFiltrate(user.id, recensioneList, user.userType)
        if (!user.imageUrl.isNullOrEmpty()) {
            // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
            Glide.with(requireContext())
                .load(user.imageUrl)
                .circleCrop()
                .into(binding.imageView)
        } else {
            // Carica l'immagine di default
            binding.imageView.setImageResource(R.drawable.baseline_image_24)
        }
    }
}
