package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class firstpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.firstpage)

        val loginButton = findViewById<Button>(R.id.login_button_index)
        val signupButton = findViewById<Button>(R.id.signup_button_index)

        loginButton.setOnClickListener{
            val intentLogin = Intent(this, login::class.java)
            startActivity(intentLogin)
        }

        signupButton.setOnClickListener{
            val intentSignup = Intent(this, signup::class.java)
            startActivity(intentSignup)
        }

    }


}