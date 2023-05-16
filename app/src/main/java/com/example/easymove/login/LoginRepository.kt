package com.example.easymove.login

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.easymove.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class LoginRepository(private val activity: LoginActivity) {

    fun autenticazione(user:String ,pass:String){
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener{
            if(it.isSuccessful){
                val intent = Intent(activity, HomeActivity::class.java)
                activity.startActivity(intent)
            }
            else{
                Toast.makeText(activity, "Email o Password non corretti", Toast.LENGTH_SHORT).show()
            }
        }
    }
}