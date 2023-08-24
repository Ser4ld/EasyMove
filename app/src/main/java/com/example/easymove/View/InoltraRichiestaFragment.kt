package com.example.easymove.View

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.easymove.R
import com.example.easymove.databinding.FragmentInoltraRichiestaBinding
import java.text.SimpleDateFormat
import java.util.*

class InoltraRichiestaFragment : Fragment() {

    private var _binding: FragmentInoltraRichiestaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInoltraRichiestaBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.textData.setOnClickListener {
            showCalendario()
        }

        val arguments = arguments
        if (arguments != null) {
            val modello = arguments.getString("modello")
            val targa = arguments.getString("targa")
            val capienza = arguments.getString("capienza")

            // Now you can use these values as needed
            binding.textViewVeicolo2.text = modello
            // Similarly, set other values to appropriate views
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