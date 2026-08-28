package com.lz.telefonoprolz.ui.contactinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.call.CallLauncher
import com.lz.telefonoprolz.data.CallLogRepository
import com.lz.telefonoprolz.databinding.ActivityContactInfoBinding
import com.lz.telefonoprolz.util.ThemeHelper
import java.util.concurrent.TimeUnit

/** Muestra cuántas llamadas se han hecho/recibido con un contacto, tal
 * como se solicitó: "cuántas llamadas se han hecho y cuántas han recibido". */
class ContactInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTo(this)
        super.onCreate(savedInstanceState)
        binding = ActivityContactInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.contact_info_title)

        val name = intent.getStringExtra(EXTRA_NAME) ?: ""
        val number = intent.getStringExtra(EXTRA_NUMBER) ?: ""

        binding.textName.text = name
        binding.textNumber.text = number

        val stats = CallLogRepository.getStatsForNumber(this, number)
        binding.textStatTotal.text = getString(R.string.stat_total_calls, stats.total)
        binding.textStatOutgoing.text = getString(R.string.stat_outgoing_calls, stats.outgoing)
        binding.textStatIncoming.text = getString(R.string.stat_incoming_calls, stats.incoming)
        binding.textStatMissed.text = getString(R.string.stat_missed_calls, stats.missed)

        val minutes = TimeUnit.SECONDS.toMinutes(stats.totalDurationSeconds)
        val seconds = stats.totalDurationSeconds % 60
        binding.textStatDuration.text = getString(R.string.stat_total_duration, "${minutes}m ${seconds}s")

        binding.btnCallFromInfo.setOnClickListener {
            CallLauncher.call(this, number)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_NUMBER = "extra_number"
    }
}
