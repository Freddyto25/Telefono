package com.lz.telefonoprolz.data

import android.content.Context
import android.provider.CallLog

data class RecentCallItem(
    val id: Long,
    val number: String,
    val displayName: String?,
    val type: Int, // CallLog.Calls.INCOMING_TYPE, OUTGOING_TYPE, MISSED_TYPE, REJECTED_TYPE
    val date: Long,
    val durationSeconds: Long
)

data class CallStats(
    val total: Int = 0,
    val outgoing: Int = 0,
    val incoming: Int = 0,
    val missed: Int = 0,
    val totalDurationSeconds: Long = 0
)

/** Encapsula el acceso al proveedor de contenido CallLog.Calls. */
object CallLogRepository {

    fun getRecentCalls(context: Context, limit: Int = 200): List<RecentCallItem> {
        val items = mutableListOf<RecentCallItem>()
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC LIMIT $limit"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(CallLog.Calls._ID)
            val numberIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
            val nameIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
            val typeIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val dateIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
            val durationIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                items.add(
                    RecentCallItem(
                        id = cursor.getLong(idIdx),
                        number = cursor.getString(numberIdx) ?: "",
                        displayName = cursor.getString(nameIdx),
                        type = cursor.getInt(typeIdx),
                        date = cursor.getLong(dateIdx),
                        durationSeconds = cursor.getLong(durationIdx)
                    )
                )
            }
        }
        return items
    }

    /** Estadísticas de llamadas para un número específico (usado en la
     * pantalla "Ver información" de un contacto). */
    fun getStatsForNumber(context: Context, number: String): CallStats {
        var total = 0
        var outgoing = 0
        var incoming = 0
        var missed = 0
        var duration = 0L

        val projection = arrayOf(CallLog.Calls.TYPE, CallLog.Calls.DURATION)
        context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            "${CallLog.Calls.NUMBER} = ?",
            arrayOf(number),
            null
        )?.use { cursor ->
            val typeIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
            val durationIdx = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)
            while (cursor.moveToNext()) {
                total++
                duration += cursor.getLong(durationIdx)
                when (cursor.getInt(typeIdx)) {
                    CallLog.Calls.OUTGOING_TYPE -> outgoing++
                    CallLog.Calls.INCOMING_TYPE -> incoming++
                    CallLog.Calls.MISSED_TYPE -> missed++
                }
            }
        }
        return CallStats(total, outgoing, incoming, missed, duration)
    }
}
