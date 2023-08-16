package com.example.easymove.annunci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.easymove.R
import com.example.easymove.databinding.ActivityAnnunciBinding
import com.example.easymove.home.HomeActivity
import com.example.easymove.model.Annuncio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AnnunciActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnnunciBinding
    private lateinit var list: ArrayList<Annuncio>
    private var db = Firebase.firestore
    private var cityOrigin: String? = null
    private var postcodeOrigin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annunci)

        val binding = ActivityAnnunciBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cityOrigin = intent.getStringExtra("cityOrigin")

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.fabButton.setOnClickListener{
            val intentBack= Intent(this, HomeActivity::class.java)
            startActivity(intentBack)
        }
        list = arrayListOf()

        db = FirebaseFirestore.getInstance()
        db.collection("vans").get().addOnSuccessListener { snapshot ->

            if (!snapshot.isEmpty) {
                for (data in snapshot.documents) {
                    val van: Annuncio? = data.toObject(Annuncio::class.java)
                    if (van != null && van.Città == cityOrigin) {
                        list.add(van)
                    }
                    else Toast.makeText(this, "Non ci sono annunci", Toast.LENGTH_SHORT).show()
                }
            }

            binding.recyclerView.adapter = MyAdapter(list)
        }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
            }
    }


}
