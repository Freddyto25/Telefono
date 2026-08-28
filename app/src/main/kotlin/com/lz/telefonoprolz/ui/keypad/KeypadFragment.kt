package com.lz.telefonoprolz.ui.keypad

import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.call.DualSimHelper
import com.lz.telefonoprolz.call.ExtensionDialer
import com.lz.telefonoprolz.call.TelefonoInCallService
import com.lz.telefonoprolz.data.ContactsRepository
import com.lz.telefonoprolz.databinding.FragmentKeypadBinding

/**
 * Teclado de marcación. Todos los botones son `Button`/`ImageButton`
 * estándar de Android (no zonas táctiles dibujadas a mano), por lo que
 * funcionan correctamente tanto con el modo de un toque como con el de
 * doble toque de los lectores de pantalla (Jieshuo, TalkBack, etc.).
 */
class KeypadFragment : Fragment() {

    private var _binding: FragmentKeypadBinding? = null
    private val binding get() = _binding!!
    private var extensionModeActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKeypadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupKeys()
        binding.btnAddExtension.setOnClickListener { insertExtensionSeparator() }
        binding.btnBackspace.setOnClickListener { backspace() }
        binding.btnCall.setOnClickListener { startCallFlow() }
        binding.btnSaveContact.setOnClickListener { saveAsContact() }

        binding.editNumber.doAfterTextChangedCompat {
            binding.btnSaveContact.visibility =
                if (it.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    private fun setupKeys() {
        val digitKeys = mapOf(
            binding.key0 to "0", binding.key1 to "1", binding.key2 to "2",
            binding.key3 to "3", binding.key4 to "4", binding.key5 to "5",
            binding.key6 to "6", binding.key7 to "7", binding.key8 to "8",
            binding.key9 to "9", binding.keyStar to "*", binding.keyHash to "#"
        )
        digitKeys.forEach { (button, digit) ->
            button.contentDescription = getString(R.string.desc_key, digit)
            button.setOnClickListener { appendDigit(digit) }
        }
    }

    private fun appendDigit(digit: String) {
        binding.editNumber.text.insert(binding.editNumber.selectionStart.coerceAtLeast(0), digit)
        // Si ya hay una llamada activa, además reenvía el tono DTMF real
        TelefonoInCallService.currentCall?.let {
            TelefonoInCallService.playDtmf(digit[0])
        }
    }

    private fun insertExtensionSeparator() {
        if (extensionModeActive) return
        extensionModeActive = true
        val text = binding.editNumber.text
        text.insert(binding.editNumber.selectionStart.coerceAtLeast(0), ExtensionDialer.EXTENSION_SEPARATOR.toString())
    }

    private fun backspace() {
        val start = binding.editNumber.selectionStart
        if (start > 0) {
            binding.editNumber.text.delete(start - 1, start)
        }
        if (binding.editNumber.text.contains(ExtensionDialer.EXTENSION_SEPARATOR).not()) {
            extensionModeActive = false
        }
    }

    private fun startCallFlow() {
        val raw = binding.editNumber.text.toString().trim()
        if (raw.isEmpty()) return
        val parsed = ExtensionDialer.parse(raw)
        if (parsed.mainNumber.isEmpty()) return

        val simAccounts = DualSimHelper.getCallCapableAccounts(requireContext())
        if (simAccounts.size > 1) {
            ChooseSimDialogFragment.newInstance(simAccounts.map { it.label }) { index ->
                placeCall(parsed.mainNumber, raw, simAccounts[index].handle)
            }.show(childFragmentManager, "choose_sim")
        } else {
            placeCall(parsed.mainNumber, raw, simAccounts.firstOrNull()?.handle)
        }
    }

    private fun placeCall(mainNumber: String, rawWithExtension: String, phoneAccountHandle: android.telecom.PhoneAccountHandle?) {
        val context = requireContext()
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CALL_PHONE), REQ_CALL_PHONE)
            return
        }
        // Guardamos el número crudo (con extensión) para que
        // TelefonoInCallService la marque automáticamente al conectar.
        TelefonoInCallService.pendingRawNumber = rawWithExtension

        val telecomManager = context.getSystemService(TelecomManager::class.java)
        val uri = Uri.fromParts("tel", mainNumber, null)
        val extras = Bundle()
        if (phoneAccountHandle != null) {
            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        }
        telecomManager?.placeCall(uri, extras)
    }

    private fun saveAsContact() {
        val raw = binding.editNumber.text.toString().trim()
        if (raw.isEmpty()) return
        val intent = android.content.Intent(requireContext(), com.lz.telefonoprolz.ui.contacts.AddEditContactActivity::class.java)
        intent.putExtra("prefill_number", ExtensionDialer.parse(raw).mainNumber)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQ_CALL_PHONE = 771
    }
}

private fun android.widget.EditText.doAfterTextChangedCompat(action: (String) -> Unit) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun afterTextChanged(s: android.text.Editable?) { action(s?.toString().orEmpty()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}
