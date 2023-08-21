package com.example.easymove.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            if(userExists){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MainFragment())
                    .commit()
            }else {

                }

            }


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IndexFragment())
                .commit()
        }
    }

}
