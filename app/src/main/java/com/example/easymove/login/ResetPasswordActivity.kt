package com.example.easymove.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.easymove.databinding.ResetpasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ResetpasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ResetpasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.resetbtn.setOnClickListener{

            auth.sendPasswordResetEmail(binding.Email.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this, "Controlla la tua Email", Toast.LENGTH_SHORT).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        finish();
                        /** overridePendingTransition(0, R.anim.animation_1);*/
                    }, 3000)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "La Email inserita non è corretta", Toast.LENGTH_SHORT).show()
                }
        }

        binding.floatingActionButton.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
