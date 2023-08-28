package com.example.easymove.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.easymove.R
import com.example.easymove.ViewModel.LoginViewModel
import com.example.easymove.repository.UserRepository
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class IndexActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val requestCode = 42 // Scegli un numero univoco per la richiesta di permessi

    private var loginViewModel = LoginViewModel(UserRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_index)

        requestPermissionsIfNecessary()
        loginViewModel.autologin { userExists, errMsg ->
            val initialFragment = if (userExists) MainFragment() else IndexFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .commit()

        }
    }

    private fun requestPermissionsIfNecessary() {
        val notGrantedPermissions = mutableListOf<String>()

        for (permission in permissions) {
            val permissionStatus = ContextCompat.checkSelfPermission(this, permission)
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission)
            }
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toTypedArray(), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == this.requestCode) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }
            if (!allPermissionsGranted) {
                finish() // Chiude l'app in caso di permessi negati
            }
        }
    }
}
