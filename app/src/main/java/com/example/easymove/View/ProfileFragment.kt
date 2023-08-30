package com.example.easymove.View

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.easymove.View.ModificaEmailFragment
import com.example.easymove.View.ResetPasswordFragment
import com.example.easymove.View.IndexActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var userId:String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        //userViewModel.fetchUserData()

        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
                binding.nomeTV.text = userData.name
                binding.cognomeTV.text = userData.surname
                binding.emailTV.text = userData.email
                binding.benvenutoTV.text = "Benvenuto " + userData.name

                userId= userData.id

                if(userData.imageUrl != ""){
                    Glide.with(requireContext())
                        .load(userData.imageUrl)
                        .circleCrop() // Rende l'immagine circolare
                        .into(binding.imageUser)
                }

                if (userData.userType == "guidatore" ){
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutProfile, ProfileGuidatoreFragment())
                        .commit()
                }else{
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutProfile, ProfileConsumatoreFragment())
                        .commit()
                }
            }
        }

        binding.modificaPasswordbtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ResetPasswordFragment())
                    .addToBackStack(null)
                    .commit()
        }

        binding.logout.setOnClickListener {
            logout()
        }

        binding.modificabtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ModificaEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.floatingButtonUpdateImage.setOnClickListener {
            openFileChooser()
        }


    }

    private fun logout() {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        //imposto lo sfodo del dialog a trasparente per poter applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{

            FirebaseAuth.getInstance().signOut()
            Toast.makeText(requireContext(), "Logout effettuato", Toast.LENGTH_SHORT).show()
            val intentLogout = Intent(requireContext(), IndexActivity::class.java)
            startActivity(intentLogout)
            requireActivity().finish()
            dialog.dismiss()
        }

        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()


    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/png" // Set the MIME type to restrict to PNG images
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data!!

            // Verifying the MIME type of the selected file
            val contentResolver = requireContext().contentResolver
            val mime = contentResolver.getType(selectedImageUri)
            if (mime == "image/png") {
                // The selected file is a PNG image
                imageUri = selectedImageUri
                userViewModel.updateImageUrl(userId, imageUri!!) { success, errMsg ->
                    if (success) {
                        Toast.makeText(requireContext(), "Immagine aggiornata con successo", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Errore durante l'aggiornamento dell'immagine", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // The selected file is not a PNG image, handle the error
                Toast.makeText(requireContext(), "Please select a PNG image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}