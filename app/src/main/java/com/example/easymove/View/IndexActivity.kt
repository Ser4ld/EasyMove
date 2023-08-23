package com.example.easymove.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easymove.R
import com.example.easymove.View.MainFragment
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.repository.UserRepository

class IndexActivity : AppCompatActivity() {

    private var loginViewModel = LoginViewModel(UserRepository())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_index)

        loginViewModel.autologin { userExists, errMsg ->
            val initialFragment = if (userExists) MainFragment() else IndexFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .commit()

        }

    }

}
