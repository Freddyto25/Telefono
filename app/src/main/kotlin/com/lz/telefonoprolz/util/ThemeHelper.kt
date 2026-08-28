package com.lz.telefonoprolz.util

import android.app.Activity
import android.content.Context
import com.lz.telefonoprolz.R

/**
 * Aplica el tema de alto contraste (fondo negro, texto amarillo) para
 * usuarios de baja visión, guardado en Ajustes ("pref_high_contrast").
 */
object ThemeHelper {

    fun applyStoredTheme(context: Context) {
        // El tema base ya se define en el manifiesto; aquí solo se resuelve
        // el override de alto contraste en las Activities (ver applyTo).
    }

    fun applyTo(activity: Activity, isInCall: Boolean = false) {
        if (PrefsHelper.isHighContrast(activity)) {
            val style = if (isInCall) {
                R.style.Theme_TelefonoProLZ_HighContrast_InCall
            } else {
                R.style.Theme_TelefonoProLZ_HighContrast
            }
            activity.setTheme(style)
        }
    }
}
