package com.lz.telefonoprolz.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lz.telefonoprolz.data.ContactsRepository
import com.lz.telefonoprolz.databinding.FragmentContactsBinding
import com.lz.telefonoprolz.ui.recents.ContactActionsBottomSheet

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ContactsAdapter
    private var allContacts: List<com.lz.telefonoprolz.data.ContactItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ContactsAdapter { contact ->
            ContactActionsBottomSheet.newInstance(contact.name, contact.number)
                .show(childFragmentManager, "contact_actions")
        }
        binding.recyclerContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerContacts.adapter = adapter

        binding.fabAddContact.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditContactActivity::class.java))
        }

        binding.editSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) = filter(s?.toString().orEmpty())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadContacts()
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }

    private fun loadContacts() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_CONTACTS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) return
        allContacts = ContactsRepository.getAllContacts(requireContext())
        adapter.submitList(allContacts)
        binding.textEmptyContacts.visibility = if (allContacts.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun filter(query: String) {
        val filtered = if (query.isBlank()) allContacts
        else allContacts.filter { it.name.contains(query, ignoreCase = true) || it.number.contains(query) }
        adapter.submitList(filtered)
        binding.textEmptyContacts.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
