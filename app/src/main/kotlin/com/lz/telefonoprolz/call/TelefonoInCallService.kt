package com.lz.telefonoprolz.call

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.VideoProfile
import com.lz.telefonoprolz.ui.incall.InCallActivity

/**
 * Servicio central de Telecom. Android lo vincula automáticamente cuando la
 * app es el marcador predeterminado. Aquí vive:
 *  - El registro de la llamada activa (expuesto como singleton para que la
 *    UI —InCallActivity— pueda leerla/controlarla).
 *  - El anunciador de llamadas (TTS) al recibir una llamada entrante.
 *  - El marcado automático de la extensión una vez la llamada está ACTIVA.
 */
class TelefonoInCallService : InCallService() {

    private val handler = Handler(Looper.getMainLooper())
    private var announcer: CallAnnouncer? = null

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            handleStateChanged(call, state)
        }
    }

    override fun onCreate() {
        super.onCreate()
        announcer = CallAnnouncer(this)
        serviceInstance = this
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        currentCall = call
        pendingExtensionDigits = ExtensionDialer.parse(pendingRawNumber ?: "").extension
        call.registerCallback(callCallback)

        if (call.state == Call.STATE_RINGING) {
            val details = call.details
            val number = details?.handle?.schemeSpecificPart
            val displayName = details?.callerDisplayName ?: details?.contactDisplayName
            announcer?.announce(displayName, number)
        }

        launchInCallScreen()
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)
        if (currentCall == call) {
            currentCall = null
            pendingExtensionDigits = null
            pendingRawNumber = null
        }
    }

    private fun handleStateChanged(call: Call, state: Int) {
        listener?.onCallStateChanged(state)
        if (state == Call.STATE_ACTIVE) {
            dialPendingExtensionIfNeeded(call)
        }
    }

    /**
     * Envía los DTMF de la extensión automáticamente, con una pequeña
     * espera para asegurar que el audio de la llamada ya esté enrutado
     * antes de marcar. Así el usuario ciego no necesita abrir el teclado
     * en plena llamada para marcar la extensión a mano.
     */
    private fun dialPendingExtensionIfNeeded(call: Call) {
        val digits = pendingExtensionDigits ?: return
        pendingExtensionDigits = null
        handler.postDelayed({
            for (digit in digits) {
                call.playDtmfTone(digit)
                call.stopDtmfTone()
            }
        }, EXTENSION_DIAL_DELAY_MS)
    }

    private fun launchInCallScreen() {
        val intent = Intent(this, InCallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        announcer?.release()
        if (serviceInstance == this) serviceInstance = null
        super.onDestroy()
    }

    interface Listener {
        fun onCallStateChanged(state: Int)
    }

    companion object {
        private const val EXTENSION_DIAL_DELAY_MS = 1200L

        /** Llamada activa actual, consultada por InCallActivity. */
        var currentCall: Call? = null
            private set

        /** Número completo (con extensión) que se marcó por última vez,
         * guardado por KeypadFragment antes de iniciar la llamada. */
        var pendingRawNumber: String? = null

        private var pendingExtensionDigits: String? = null

        var listener: Listener? = null

        private var serviceInstance: TelefonoInCallService? = null

        fun answer() {
            currentCall?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        fun reject() {
            currentCall?.reject(false, null)
        }

        fun hangup() {
            currentCall?.disconnect()
        }

        fun setMuted(muted: Boolean) {
            serviceInstance?.setMuted(muted)
        }

        fun setSpeakerOn(on: Boolean) {
            val route = if (on) android.telecom.CallAudioState.ROUTE_SPEAKER
                        else android.telecom.CallAudioState.ROUTE_EARPIECE
            serviceInstance?.setAudioRoute(route)
        }

        fun playDtmf(digit: Char) {
            currentCall?.playDtmfTone(digit)
            currentCall?.stopDtmfTone()
        }
    }
}
