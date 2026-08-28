package com.lz.telefonoprolz.ui.contacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.data.ContactsRepository
import com.lz.telefonoprolz.databinding.ActivityAddEditContactBinding
import com.lz.telefonoprolz.util.ThemeHelper

class AddEditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditContactBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeHelper.applyTo(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_contact_title)

        intent.getStringExtra("prefill_number")?.let {
            binding.editContactNumber.setText(it)
        }

        binding.btnSaveNewContact.setOnClickListener {
            val name = binding.editContactName.text.toString().trim()
            val number = binding.editContactNumber.text.toString().trim()
            if (name.isNotEmpty() && number.isNotEmpty()) {
                ContactsRepository.insertContact(this, name, number)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
