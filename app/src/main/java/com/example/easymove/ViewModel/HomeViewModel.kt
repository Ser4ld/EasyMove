package com.example.easymove.ViewModel

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.easymove.R
import com.example.easymove.model.MapData
import com.example.easymove.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.util.*

class HomeViewModel () : ViewModel() {


    // Funzione che verifica che gli indirizzi di partenza e destinazione siano impostati correttamente.
    fun checkFormEditTexts(
        originData: MapData,
        destinationData: MapData,
        originEditText: EditText,
        destinationEditText: EditText,
        callback: (Boolean, String?) -> Unit
    ) {
        // Ottiene gli indirizzi dalle EditText
        val origin = originEditText.text.toString()
        val destination = destinationEditText.text.toString()

        // Verifica che entrambi gli indirizzi siano impostati e non vuoti
        if (originData!=null && destinationData != null && origin.isNotEmpty() && destination.isNotEmpty()) {
            callback(true, null)
        } else {
            callback(false, "Indirizzo di partenza e/o di destinazione non impostati")
        }
    }

}
