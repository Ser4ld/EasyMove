package com.example.easymove

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easymove.databinding.HomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

    }
}