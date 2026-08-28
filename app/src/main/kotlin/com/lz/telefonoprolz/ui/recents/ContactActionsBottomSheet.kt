package com.lz.telefonoprolz.ui.recents

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lz.telefonoprolz.call.CallLauncher
import com.lz.telefonoprolz.databinding.BottomsheetContactActionsBinding
import com.lz.telefonoprolz.ui.contactinfo.ContactInfoActivity

/**
 * Hoja de acciones al tocar un contacto/llamada: Llamar, Enviar mensaje,
 * Ver información — tal como en el marcador de Samsung.
 */
class ContactActionsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetContactActionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetContactActionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString(ARG_NAME) ?: ""
        val number = arguments?.getString(ARG_NUMBER) ?: ""

        binding.textSheetTitle.text = name

        binding.rowCall.setOnClickListener {
            CallLauncher.call(requireContext(), number)
            dismiss()
        }
        binding.rowMessage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number"))
            startActivity(intent)
            dismiss()
        }
        binding.rowInfo.setOnClickListener {
            val intent = Intent(requireContext(), ContactInfoActivity::class.java)
            intent.putExtra(ContactInfoActivity.EXTRA_NAME, name)
            intent.putExtra(ContactInfoActivity.EXTRA_NUMBER, number)
            startActivity(intent)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_NUMBER = "arg_number"

        fun newInstance(name: String, number: String): ContactActionsBottomSheet {
            return ContactActionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_NUMBER, number)
                }
            }
        }
    }
}
