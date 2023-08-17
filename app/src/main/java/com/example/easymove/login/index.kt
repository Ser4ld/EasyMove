package com.example.easymove.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.example.easymove.R
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.databinding.IndexBinding
import com.example.easymove.model.User
import com.example.easymove.registrazione.SignupActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore

class index : AppCompatActivity() {

    private lateinit var binding: IndexBinding
    private lateinit var LogViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IndexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogViewModel = LoginViewModel(this , User(FirebaseFirestore.getInstance()))

        LogViewModel.autoLogin()


        val animation_1 = AnimationUtils.loadAnimation(this, R.anim.animation_1)
        val animation_2 = AnimationUtils.loadAnimation(this, R.anim.animation_2)
        val animation_3 = AnimationUtils.loadAnimation(this, R.anim.animation_3)
        val animation_4 = AnimationUtils.loadAnimation(this, R.anim.animation_4)

        binding.loginButtonIndex.startAnimation(animation_1)
        binding.cardviewIndex.startAnimation(animation_2)
        binding.loginButtonIndex.startAnimation(animation_3)
        binding.signupButtonIndex.startAnimation(animation_3)
        binding.textViewHeader.startAnimation(animation_4)
        binding.textViewSubHeader.startAnimation(animation_4)

        binding.loginButtonIndex.setOnClickListener{

            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
        }

        binding.signupButtonIndex.setOnClickListener{
            val intentSignup = Intent(this, SignupActivity::class.java)
            startActivity(intentSignup)
        }


    }

}
