package com.example.easymove

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class splashpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splashpage)

        val scrittaAccedi = findViewById<TextView>(R.id.login)
        val scrittaRegistrati = findViewById<TextView>(R.id.signup)

        scrittaAccedi.setOnClickListener{
            val IntentAccedi = Intent(this, login::class.java)
            startActivity(IntentAccedi)
        }

        scrittaRegistrati.setOnClickListener{
            val IntentRegistrati = Intent(this, signup::class.java)
            startActivity(IntentRegistrati)
        }

    }


}