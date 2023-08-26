package com.example.easymove.View

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.databinding.FragmentInoltraRichiestaBinding
import java.text.SimpleDateFormat
import java.util.*

class InoltraRichiestaFragment : Fragment() {

    private var _binding: FragmentInoltraRichiestaBinding? = null
    private val binding get() = _binding!!
    private lateinit var userViewModel: UserViewModel
    private lateinit var richiestaViewModel: RichiestaViewModel


    private lateinit var modello: String
    private lateinit var targa: String
    private lateinit var capienza: String
    private lateinit var idGuidatore: String
    private lateinit var destination: String
    private lateinit var origin: String
    private lateinit var userId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInoltraRichiestaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        richiestaViewModel = ViewModelProvider(requireActivity()).get(RichiestaViewModel::class.java)


        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.textData.setOnClickListener {
            showCalendario()
        }




        val argument = arguments
        if (argument != null) {
            modello = argument.getString("modello").toString()
            targa = argument.getString("targa").toString()
            capienza = argument.getString("capienza").toString()
            idGuidatore = argument.getString("id_guidatore").toString()
            destination = argument.getString("destination").toString()
            origin =  argument.getString("origin").toString()
            // Now you can use these values as needed
            binding.textViewVeicolo2.text = modello
            binding.textViewDestination2.text = destination
            // Similarly, set other values to appropriate views
        }

        userViewModel.allUsersLiveData.observe(
            viewLifecycleOwner,
            ) { userList ->
            val foundUser = userList.firstOrNull { user -> user.id == idGuidatore }
            if(foundUser!= null){
                binding.textViewGuidatore2.text= "${foundUser.name} ${foundUser.surname}"
            }
        }



        userViewModel.userDataLiveData.observe(
            viewLifecycleOwner,
        ) { userData ->
            if (userData != null) {
               userId= userData.id
            }
        }

        binding.richiestabtn.setOnClickListener{
                // Verifica se sono stati inseriti tutti i dati necessari
            richiestaViewModel.inoltraRichiesta(
                idGuidatore,
                userId,
                targa,
                origin,
                destination,
                binding.textData.text.toString(),
                binding.textDescription.text.toString(),
                ){success, message ->
                    if(success){
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()

                    }else{
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        //no success
                    }

                }

            }



    }


    private fun showCalendario() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            //_ -> usato per ignorare il primo parametro che rappresenta il datePicker
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                //setta la data scelta dall'utente
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                //formattazione data
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                //impostazione data scelta nell'editText
                binding.textData.setText(formattedDate)
            },
            //rappresentano year -> anno corrente, month-> mese corrente, day -> giorno corrente (valori preimpostati quando viene aperto il DataPicker)
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}