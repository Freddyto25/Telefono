package com.lz.telefonoprolz.data

import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract

data class ContactItem(
    val id: Long,
    val name: String,
    val number: String
)

object ContactsRepository {

    fun getAllContacts(context: Context): List<ContactItem> {
        val items = mutableListOf<ContactItem>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )?.use { cursor ->
            val idIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                items.add(
                    ContactItem(
                        id = cursor.getLong(idIdx),
                        name = cursor.getString(nameIdx) ?: "",
                        number = cursor.getString(numberIdx) ?: ""
                    )
                )
            }
        }
        return items
    }

    fun findDisplayNameForNumber(context: Context, number: String): String? {
        val uri = android.net.Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            android.net.Uri.encode(number)
        )
        context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null, null, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            }
        }
        return null
    }

    /** Inserta un contacto nuevo directamente vía RawContacts (sin lanzar
     * el editor de contactos del sistema), útil para el flujo accesible
     * "Guardar como contacto" desde el teclado. */
    fun insertContact(context: Context, name: String, number: String) {
        val resolver = context.contentResolver
        val rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, ContentValues())
        val rawContactId = rawContactUri?.lastPathSegment?.toLong() ?: return

        val nameValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
        }
        resolver.insert(ContactsContract.Data.CONTENT_URI, nameValues)

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
            put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
        }
        resolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)
    }
}
