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
import com.example.easymove.MapBox.inputMethodManager
import com.example.easymove.R
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.ui.view.SearchResultsView
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import java.util.*

class HomeViewModel : ViewModel() {

    private var origin: Point = Point.fromLngLat(0.0, 0.0)
    private var destination : Point = Point.fromLngLat(0.0, 0.0)

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun fetchAndSetTipoutente(userId: String?, callback: (Boolean) -> Unit) {
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val tipoutente = documentSnapshot.getString("tipoutente")
                        val isGuidatore = tipoutente == "guidatore"
                        callback(isGuidatore)
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Errore durante la lettura dal Firestore", exception)
                    callback(false)
                }
        }
    }

    fun checkActionId(actionId: Int): Boolean {
        return actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
    }

    fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun getDistancePoints(): String? {
        if ( origin.latitude() != 0.0 && origin.longitude()!=0.0 && destination.latitude() != 0.0 && destination.longitude()!=0.0){
            val distanza = TurfMeasurement.distance(origin, destination, TurfConstants.UNIT_KILOMETERS)
            val distanzaFormattata = String.format(Locale.getDefault(), "%.2f", distanza)
            var distanzaStringa = "Distanza: $distanzaFormattata Km"
            return distanzaStringa
        }
        return null
    }




}
