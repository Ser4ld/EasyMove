package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        val scrittaAccedi = findViewById<TextView>(R.id.text_login_2)

        scrittaAccedi.setOnClickListener{
            val IntentSignUp = Intent(this, login::class.java)
            startActivity(IntentSignUp)
        }
    }


}