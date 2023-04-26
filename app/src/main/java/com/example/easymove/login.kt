package com.example.easymove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val loginbtn = findViewById<Button>(R.id.login)
        val user = findViewById<EditText>(R.id.Email)
        val pass = findViewById<EditText>(R.id.Password)

        loginbtn.setOnClickListener {

            if (!(user.getText().toString().isEmpty()) && !(pass.getText().toString().isEmpty())) {

                if (user.getText().toString().equals("admin") && pass.getText().toString()
                        .equals("pass")
                ) {

                    val Intent = Intent(this, MainActivity::class.java)
                    startActivity(Intent)

                } else {

                    Toast.makeText(this, "Username o Password errati", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Username o Password non inseriti", Toast.LENGTH_SHORT).show()
            }

        }

    }
}