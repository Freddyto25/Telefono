package com.lz.telefonoprolz.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

/**
 * Se conserva por compatibilidad con Android 8–9 (donde algunas ROMs aún
 * disparan NEW_OUTGOING_CALL incluso siendo marcador predeterminado). No
 * modifica el número: solo permite futuras integraciones (registro extra,
 * notificar a Tasker en llamadas salientes) sin romper la marcación nativa
 * que ya gestiona TelefonoInCallService a través de TelecomManager.
 */
class OutgoingCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_NEW_OUTGOING_CALL) return
        val number = intent.getStringExtra(TelephonyManager.EXTRA_PHONE_NUMBER) ?: return
        val outIntent = Intent(TelefonoCallScreeningService.ACTION_INCOMING_CALL).apply {
            putExtra(TelefonoCallScreeningService.EXTRA_NUMBER, number)
            putExtra("is_outgoing", true)
        }
        context.sendBroadcast(outIntent)
    }
}
