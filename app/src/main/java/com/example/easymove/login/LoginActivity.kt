package com.example.easymove.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.LoginBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.model.User
import com.example.easymove.registrazione.SignupActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private lateinit var LogViewModel: LoginViewModel
    private val loginRepository = LoginRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogViewModel = LoginViewModel(User(FirebaseFirestore.getInstance()))

        loginRepository.autoLogin()
        /*intent per passare alle schermate di RecuperoPassword, index (cliccando il backbutton) e pagina di registrazione */

        binding.passwordDimenticata.setOnClickListener{
            val intentPassDimenticata= Intent(this, ResetPasswordActivity::class.java)
            startActivity(intentPassDimenticata)
        }
        binding.floatingActionButton.setOnClickListener{
            onBackPressed()
        }
        binding.textSingUp2.setOnClickListener{
            val intentSignUp = Intent(this, SignupActivity::class.java)
            startActivity(intentSignUp)
        }

        binding.login.setOnClickListener {
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()

            LogViewModel.login(
                email,
                password
            ){ success, message ->
                if (success) {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
