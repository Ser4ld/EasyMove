package com.example.easymove.login

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.easymove.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class LoginRepository(private val activity: AppCompatActivity) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun goHome(){
        val intentAutoLogin = Intent(activity, HomeActivity::class.java)
        activity.startActivity(intentAutoLogin)
        activity.finish()

    }

    fun autenticazione(user:String ,pass:String){
        firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener{
            if(it.isSuccessful){
                goHome()
            }
            else{
                Toast.makeText(activity, "Email o Password non corretti", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun autoLogin() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            goHome()
        }
    }



}