package com.example.easymove.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.easymove.R
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.resetpassword)


        val backbutton= findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.floatingActionButton
        )
        val etPassword = findViewById<TextView>(R.id.Email)
        val resetbtn = findViewById<Button>(R.id.resetbtn)

        auth= FirebaseAuth.getInstance()

        resetbtn.setOnClickListener{

            val sPassword= etPassword.text.toString()
            auth.sendPasswordResetEmail(sPassword)
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

        backbutton.setOnClickListener{
            val intentBack= Intent(this, LoginActivity::class.java)
            startActivity(intentBack)
            finish()
        }
    }
}