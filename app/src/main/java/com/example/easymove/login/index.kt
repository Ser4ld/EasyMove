package com.example.easymove.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.example.easymove.R
import com.example.easymove.View.IndexFragment
import com.example.easymove.View.MainFragment
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.repository.UserRepository

class index : AppCompatActivity() {

    private var loginViewModel = LoginViewModel(UserRepository())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.index)

        loginViewModel.autologin { userExists, errMsg ->
            val initialFragment = if (userExists) MainFragment() else IndexFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .addToBackStack(null)
                .commit()

        }

    }

}
