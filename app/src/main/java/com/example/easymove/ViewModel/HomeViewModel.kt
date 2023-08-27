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
import com.example.easymove.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.util.*

class HomeViewModel () : ViewModel() {

    private var userRepository = UserRepository()

    fun fetchAndSetTipoutente( callback: (Boolean) -> Unit) {
        userRepository.getUserData { userData ->
            if (userData != null) {
                val isGuidatore = userData.userType == "guidatore"
                callback(isGuidatore)
            } else {
                callback(false)
            }
        }
    }

    fun checkActionId(actionId: Int): Boolean {
        return actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
    }

    fun checkFormEditTexts(
        originEditText: EditText,
        destinationEditText: EditText,
        callback: (Boolean, String?) -> Unit
    ) {
        val origin = originEditText.text.toString()
        val destination = destinationEditText.text.toString()

        if (origin.isNotEmpty() && destination.isNotEmpty()) {
            callback(true, null)
        } else {
            callback(false, "Indirizzo di partenza e/o di destinazione non impostati")
        }
    }



}
