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

    // Array delle autorizzazioni richieste dall'applicazione
    private val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    // Codice univoco di richiesta per la gestione delle autorizzazioni
    private val requestCode = 42

    // Istanza del loginviewmodel verrà utilizzata per l'auto login
    private var loginViewModel = LoginViewModel(UserRepository())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Imposta il layout dell'activity utilizzando il file XML activity_index
        setContentView(R.layout.activity_index)

        // Richiede le autorizzazioni necessarie
        requestPermissionsIfNecessary()

        // Esegue il tentativo di login automatico tramite il ViewModel
        loginViewModel.autologin { userExists, errMsg ->

            // Determina il fragment iniziale in base all'esistenza dell'utente
            val initialFragment = if (userExists) MainFragment() else IndexFragment()

            // Sostituisce la vista corrente con la vista iniziale scelta in base all'esistenza dell'utente
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, initialFragment)
                .commit()

        }
    }


    // Funzione che va prima a verificare che siano state accettate le autorizzazioni
    // nel caso non lo fossero le aggiunge alla lista dei permessi non accettati
    // nel caso in cui la lista sia non vuota richiede le autorizzazioni mancanti
    private fun requestPermissionsIfNecessary() {
        // Lista delle autorizzazioni che non sono state concesse
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


    // Metodo chiamato quando l'utente risponde alla richiesta di permessi
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // controllo condizionale assicura che il blocco di codice successivo venga eseguito
        // solo se la risposta corrisponde alla richiesta di permessi dell'app e non a un'altra richiesta di permessi
        if (requestCode == this.requestCode) {
            var allPermissionsGranted = true
            // Itera attraverso i risultati delle richieste di permessi
            for (result in grantResults) {
                // Se almeno un permesso non è stato concesso, imposta la variabile a false e interrompi il ciclo
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            // Se non sono stati concessi tutti i permessi, chiude l'app tramite il metodo finish()
            if (!allPermissionsGranted) {
                finish()
            }
        }
    }
}
