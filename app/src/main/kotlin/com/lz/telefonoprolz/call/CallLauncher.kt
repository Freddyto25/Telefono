package com.lz.telefonoprolz.call

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat

/** Punto único para iniciar una llamada desde cualquier pantalla de la app
 * (recientes, contactos, hoja de acciones, info de contacto), respetando
 * el flujo de doble SIM cuando corresponde. */
object CallLauncher {

    fun call(context: Context, rawNumberWithExtension: String) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val parsed = ExtensionDialer.parse(rawNumberWithExtension)
        if (parsed.mainNumber.isEmpty()) return

        val accounts = DualSimHelper.getCallCapableAccounts(context)
        TelefonoInCallService.pendingRawNumber = rawNumberWithExtension

        val telecomManager = context.getSystemService(TelecomManager::class.java)
        val uri = Uri.fromParts("tel", parsed.mainNumber, null)
        val extras = Bundle()
        if (accounts.size == 1) {
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, accounts.first().handle)
        }
        telecomManager?.placeCall(uri, extras)
    }
}
