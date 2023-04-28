package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class firstpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.firstpage)

        val scrittaAccedi = findViewById<TextView>(R.id.login)
        val scrittaRegistrati = findViewById<TextView>(R.id.signup)

        scrittaAccedi.setOnClickListener{
            val intentAccedi = Intent(this, login::class.java)
            startActivity(intentAccedi)
        }

        scrittaRegistrati.setOnClickListener{
            val intentRegistrati = Intent(this, signup::class.java)
            startActivity(intentRegistrati)
        }

    }


}