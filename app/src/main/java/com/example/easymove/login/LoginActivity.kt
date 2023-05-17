package com.example.easymove.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easymove.databinding.LoginBinding
import com.example.easymove.registrazione.SignupActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginBinding
    private val loginRepository = LoginRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


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

            if (binding.Email.text.toString().isNotEmpty() && binding.Password.text.toString().isNotEmpty()) {
                loginRepository.autenticazione(binding.Email.text.toString(), binding.Password.text.toString())
            }
            else{
                Toast.makeText(this, "Email o Password non inseriti", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
