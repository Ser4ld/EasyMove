package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView

class firstpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firstpage)

        val loginButton = findViewById<Button>(R.id.login_button_index)
        val signupButton = findViewById<Button>(R.id.signup_button_index)
        val logo = findViewById<ImageView>(R.id.EasyMoveLogo)
        val cardview = findViewById<MaterialCardView>(R.id.cardview_index)

        val animation_1 = AnimationUtils.loadAnimation(this, R.anim.animation_1)
        val animation_2 = AnimationUtils.loadAnimation(this, R.anim.animation_2)
        val animation_3 = AnimationUtils.loadAnimation(this, R.anim.animation_3)

        logo.startAnimation(animation_1)
        cardview.startAnimation(animation_2)
        loginButton.startAnimation(animation_3)
        signupButton.startAnimation(animation_3)

        loginButton.setOnClickListener{
            val intentLogin = Intent(this, login::class.java)
            startActivity(intentLogin)
        }

        signupButton.setOnClickListener{
            val intentSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentSignup)
        }

    }


}