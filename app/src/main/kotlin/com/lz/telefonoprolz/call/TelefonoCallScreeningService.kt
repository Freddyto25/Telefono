package com.lz.telefonoprolz.call

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.CallScreeningService.CallResponse
import com.lz.telefonoprolz.util.PrefsHelper

/**
 * Servicio de filtrado de llamadas. Bloquea números marcados como spam por
 * el propio usuario y, en cualquier caso, envía un broadcast explícito con
 * los datos de la llamada entrante para que Tasker (u otra app de
 * automatización) pueda construir sus propios perfiles de filtrado de spam
 * ("Evento del sistema" → "Intent recibido" → acción
 * com.lz.telefonoprolz.ACTION_INCOMING_CALL).
 */
class TelefonoCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val number = callDetails.handle?.schemeSpecificPart

        broadcastToTasker(number)

        val shouldBlock = PrefsHelper.isSpamFilterEnabled(this) &&
            number != null && PrefsHelper.isNumberBlocked(this, number)

        val responseBuilder = CallResponse.Builder()
        if (shouldBlock) {
            responseBuilder
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(true)
        }
        respondToCall(callDetails, responseBuilder.build())
    }

    private fun broadcastToTasker(number: String?) {
        val intent = Intent(ACTION_INCOMING_CALL).apply {
            putExtra(EXTRA_NUMBER, number)
            setPackage(null)
        }
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_INCOMING_CALL = "com.lz.telefonoprolz.ACTION_INCOMING_CALL"
        const val EXTRA_NUMBER = "extra_number"
    }
}
