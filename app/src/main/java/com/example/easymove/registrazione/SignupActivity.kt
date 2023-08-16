package com.example.easymove.registrazione

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easymove.ViewModel.SignupViewModel
import com.example.easymove.databinding.SignupBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.login.LoginActivity
import com.example.easymove.login.index
import com.example.easymove.model.User
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: SignupBinding
    var tipoutente = "consumatore" // Esempio fisso, potrebbe variare a seconda del toggleButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = SignupViewModel(User(FirebaseFirestore.getInstance()))

        binding.floatingActionButton.setOnClickListener {
            val intentBack = Intent(this, index::class.java)
            startActivity(intentBack)
            finish()
        }

        binding.textLogin2.setOnClickListener {
            val intentSignUp = Intent(this, LoginActivity::class.java)
            startActivity(intentSignUp)
            finish()
        }


        binding.signup.setOnClickListener {
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()
            val nome = binding.Nome.text.toString()
            val cognome = binding.Cognome.text.toString()
            val repeatPassword = binding.RepeatPassword.text.toString()


            viewModel.signUp(
                email,
                password,
                repeatPassword,
                nome,
                cognome,
                tipoutente
            ) { success, message ->
                if (success) {
                    val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->
            tipoutente = if (isChecked) {
                "guidatore"
            } else {
                "consumatore"
            }
        }

    }
}
