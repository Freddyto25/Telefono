package com.lz.telefonoprolz.call

import android.content.Context
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager

data class SimAccount(
    val handle: PhoneAccountHandle,
    val label: String
)

/**
 * Detecta si el teléfono tiene más de una línea (doble SIM) usando la API
 * de Telecom (funciona igual sin importar el fabricante, a diferencia de
 * SubscriptionManager que a veces requiere permisos extra por marca).
 */
object DualSimHelper {

    fun getCallCapableAccounts(context: Context): List<SimAccount> {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            ?: return emptyList()
        return try {
            telecomManager.callCapablePhoneAccounts.mapNotNull { handle ->
                val account = telecomManager.getPhoneAccount(handle) ?: return@mapNotNull null
                SimAccount(handle, account.label?.toString() ?: handle.id)
            }
        } catch (se: SecurityException) {
            emptyList()
        }
    }

    fun hasMultipleSims(context: Context): Boolean = getCallCapableAccounts(context).size > 1
}
