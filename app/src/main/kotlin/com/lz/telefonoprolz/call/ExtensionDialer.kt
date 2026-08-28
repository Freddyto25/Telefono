package com.lz.telefonoprolz.call

/**
 * Un mismo teclado permite escribir "número + extensión" usando un
 * separador especial (';') que el usuario inserta con el botón
 * "Agregar extensión". Esta clase separa ambas partes para que:
 *   1) Se marque solo el número principal con TelecomManager.
 *   2) Cuando la llamada quede ACTIVA, se envíen automáticamente los
 *      tonos DTMF de la extensión (ver TelefonoInCallService), sin que
 *      la persona ciega tenga que abrir el teclado en pleno tono de
 *      espera y marcar manualmente.
 */
object ExtensionDialer {

    const val EXTENSION_SEPARATOR = ';'

    data class ParsedNumber(val mainNumber: String, val extension: String?)

    fun parse(rawInput: String): ParsedNumber {
        val idx = rawInput.indexOf(EXTENSION_SEPARATOR)
        return if (idx == -1) {
            ParsedNumber(rawInput.trim(), null)
        } else {
            val main = rawInput.substring(0, idx).trim()
            val ext = rawInput.substring(idx + 1).trim().takeIf { it.isNotEmpty() }
            ParsedNumber(main, ext)
        }
    }
}
