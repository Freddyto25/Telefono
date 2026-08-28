package com.lz.telefonoprolz.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object PrefsHelper {

    private const val KEY_HIGH_CONTRAST = "pref_high_contrast"
    private const val KEY_CALL_ANNOUNCER = "pref_call_announcer"
    private const val KEY_VOLUME_ANSWER = "pref_volume_answer"
    private const val KEY_POWER_END_CALL = "pref_power_end_call"
    private const val KEY_ANSWER_MODE = "pref_answer_mode"
    private const val KEY_SPAM_FILTER = "pref_spam_filter"

    private fun prefs(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun isHighContrast(context: Context): Boolean =
        prefs(context).getBoolean(KEY_HIGH_CONTRAST, false)

    fun isCallAnnouncerEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CALL_ANNOUNCER, true)

    fun isVolumeAnswerEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_VOLUME_ANSWER, true)

    fun isPowerEndCallEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_POWER_END_CALL, false)

    /** "gesture" o "touch" */
    fun answerMode(context: Context): String =
        prefs(context).getString(KEY_ANSWER_MODE, "touch") ?: "touch"

    fun isSpamFilterEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SPAM_FILTER, false)

    fun isNumberBlocked(context: Context, number: String): Boolean =
        prefs(context).getStringSet("blocked_numbers", emptySet())?.contains(number) ?: false

    fun blockNumber(context: Context, number: String) {
        val set = HashSet(prefs(context).getStringSet("blocked_numbers", emptySet()) ?: emptySet())
        set.add(number)
        prefs(context).edit().putStringSet("blocked_numbers", set).apply()
    }
}
