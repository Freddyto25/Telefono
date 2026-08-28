package com.lz.telefonoprolz.call

import android.content.Context
import android.provider.Settings

/**
 * Controla el ajuste protegido del sistema que permite finalizar una
 * llamada con la tecla de encendido. Requiere WRITE_SECURE_SETTINGS, que
 * solo puede concederse mediante ADB (ver README.md). Si el permiso no fue
 * otorgado, las escrituras simplemente fallan de forma silenciosa y se
 * informa al usuario desde Ajustes.
 */
object PowerButtonEndCallHelper {

    // Valores documentados de Settings.System.INCALL_POWER_BUTTON_BEHAVIOR
    private const val INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF = 0
    private const val INCALL_POWER_BUTTON_BEHAVIOR_HANGUP = 1
    private const val SETTING_KEY = "incall_power_button_behavior"

    fun setEndCallOnPower(context: Context, enabled: Boolean): Boolean {
        return try {
            Settings.System.putInt(
                context.contentResolver,
                SETTING_KEY,
                if (enabled) INCALL_POWER_BUTTON_BEHAVIOR_HANGUP else INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF
            )
            true
        } catch (se: SecurityException) {
            false
        }
    }
}
