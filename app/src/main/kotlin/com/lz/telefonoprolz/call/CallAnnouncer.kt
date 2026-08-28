package com.lz.telefonoprolz.call

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import java.util.Locale
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.util.PrefsHelper

/**
 * Anuncia por voz quién está llamando. Complementa (no reemplaza) al
 * lector de pantalla: sirve para el caso en que la pantalla de llamada
 * entrante aún no ha tomado el foco de accesibilidad, o para usuarios con
 * baja visión que prefieren escuchar el nombre de inmediato.
 */
class CallAnnouncer(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null

    fun announce(displayName: String?, number: String?) {
        if (!PrefsHelper.isCallAnnouncerEnabled(appContext)) return
        val textToSay = when {
            !displayName.isNullOrBlank() -> appContext.getString(R.string.incall_incoming_from, displayName)
            !number.isNullOrBlank() -> appContext.getString(R.string.incall_incoming_from, number)
            else -> return
        }
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
                tts?.speak(textToSay, QUEUE_FLUSH, null, "telefono_pro_announce")
            }
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
