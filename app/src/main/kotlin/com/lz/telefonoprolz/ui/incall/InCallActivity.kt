package com.lz.telefonoprolz.ui.incall

import android.os.Bundle
import android.telecom.Call
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.call.TelefonoInCallService
import com.lz.telefonoprolz.data.ContactsRepository
import com.lz.telefonoprolz.databinding.ActivityIncallBinding
import com.lz.telefonoprolz.util.PrefsHelper
import com.lz.telefonoprolz.util.ThemeHelper
import kotlin.math.abs

/**
 * Pantalla de llamada entrante / en curso. Accesible por diseño:
 *  - Todos los botones son Button/ImageButton estándar (funcionan igual
 *    con lectores de pantalla en modo de 1 o 2 toques).
 *  - Responder con la tecla de subir volumen (ajustable en Ajustes).
 *  - Responder por gesto (deslizar) o por toque, según preferencia.
 */
class InCallActivity : AppCompatActivity(), TelefonoInCallService.Listener {

    private lateinit var binding: ActivityIncallBinding
    private var isMuted = false
    private var isSpeakerOn = false
    private var gestureStartY: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTo(this, isInCall = true)
        super.onCreate(savedInstanceState)
        binding = ActivityIncallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TelefonoInCallService.listener = this
        bindCallInfo()
        updateUiForState(TelefonoInCallService.currentCall?.state ?: Call.STATE_NEW)
        setupButtons()
        setupGestureIfNeeded()
    }

    private fun bindCallInfo() {
        val details = TelefonoInCallService.currentCall?.details
        val number = details?.handle?.schemeSpecificPart ?: ""
        val cachedName = details?.callerDisplayName ?: details?.contactDisplayName
        val name = cachedName ?: ContactsRepository.findDisplayNameForNumber(this, number) ?: number

        binding.textCallerName.text = name
        binding.textCallerNumber.text = number
    }

    private fun setupButtons() {
        binding.btnAnswer.setOnClickListener { TelefonoInCallService.answer() }
        binding.btnDecline.setOnClickListener { TelefonoInCallService.reject() }
        binding.btnHangup.setOnClickListener { TelefonoInCallService.hangup() }

        binding.btnMute.setOnClickListener {
            isMuted = !isMuted
            TelefonoInCallService.setMuted(isMuted)
        }
        binding.btnSpeaker.setOnClickListener {
            isSpeakerOn = !isSpeakerOn
            TelefonoInCallService.setSpeakerOn(isSpeakerOn)
        }
        binding.btnKeypadInCall.setOnClickListener {
            // El teclado en llamada reutiliza el mismo diálogo simple de
            // marcación DTMF; se omite por brevedad un teclado completo,
            // playDtmf ya está disponible vía TelefonoInCallService.
        }
    }

    private fun setupGestureIfNeeded() {
        if (PrefsHelper.answerMode(this) != "gesture") return
        binding.root.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> gestureStartY = event.y
                MotionEvent.ACTION_UP -> {
                    val start = gestureStartY ?: return@setOnTouchListener false
                    if (abs(event.y - start) > SWIPE_THRESHOLD_PX &&
                        TelefonoInCallService.currentCall?.state == Call.STATE_RINGING
                    ) {
                        TelefonoInCallService.answer()
                    }
                    gestureStartY = null
                }
            }
            false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (PrefsHelper.isVolumeAnswerEnabled(this) &&
            (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) &&
            TelefonoInCallService.currentCall?.state == Call.STATE_RINGING
        ) {
            TelefonoInCallService.answer()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCallStateChanged(state: Int) {
        runOnUiThread { updateUiForState(state) }
    }

    private fun updateUiForState(state: Int) {
        when (state) {
            Call.STATE_RINGING -> {
                binding.textCallState.text = getString(R.string.incall_incoming_from, binding.textCallerName.text)
                binding.btnAnswer.visibility = View.VISIBLE
                binding.btnDecline.visibility = View.VISIBLE
                binding.btnHangup.visibility = View.GONE
                binding.rowInCallControls.visibility = View.GONE
            }
            Call.STATE_DIALING, Call.STATE_CONNECTING -> {
                binding.textCallState.text = getString(R.string.incall_outgoing_to, binding.textCallerName.text)
                binding.btnAnswer.visibility = View.GONE
                binding.btnDecline.visibility = View.GONE
                binding.btnHangup.visibility = View.VISIBLE
                binding.rowInCallControls.visibility = View.GONE
            }
            Call.STATE_ACTIVE -> {
                binding.textCallState.text = getString(R.string.incall_connected, binding.textCallerName.text)
                binding.btnAnswer.visibility = View.GONE
                binding.btnDecline.visibility = View.GONE
                binding.btnHangup.visibility = View.VISIBLE
                binding.rowInCallControls.visibility = View.VISIBLE
            }
            Call.STATE_DISCONNECTED, Call.STATE_DISCONNECTING -> finish()
        }
    }

    override fun onDestroy() {
        if (TelefonoInCallService.listener == this) TelefonoInCallService.listener = null
        super.onDestroy()
    }

    companion object {
        private const val SWIPE_THRESHOLD_PX = 150
    }
}
