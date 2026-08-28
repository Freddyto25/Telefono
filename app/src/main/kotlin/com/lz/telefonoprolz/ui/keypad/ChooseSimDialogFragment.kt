package com.lz.telefonoprolz.ui.keypad

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.databinding.DialogChooseSimBinding
import com.lz.telefonoprolz.databinding.ItemSimBinding

/**
 * Diálogo accesible para elegir con qué línea (SIM 1 / SIM 2) realizar la
 * llamada, mostrado automáticamente cuando el dispositivo tiene doble SIM.
 */
class ChooseSimDialogFragment : DialogFragment() {

    private var labels: List<String> = emptyList()
    private var onChosen: ((Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogChooseSimBinding.inflate(LayoutInflater.from(requireContext()))
        binding.recyclerSims.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSims.adapter = object : RecyclerView.Adapter<SimViewHolder>() {
            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SimViewHolder {
                val itemBinding = ItemSimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return SimViewHolder(itemBinding)
            }
            override fun onBindViewHolder(holder: SimViewHolder, position: Int) {
                holder.bind(labels[position], position)
            }
            override fun getItemCount() = labels.size
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_sim_title)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private inner class SimViewHolder(private val binding: ItemSimBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String, index: Int) {
            binding.textSimLabel.text = label
            binding.root.setOnClickListener {
                onChosen?.invoke(index)
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(labels: List<String>, onChosen: (Int) -> Unit): ChooseSimDialogFragment {
            return ChooseSimDialogFragment().apply {
                this.labels = labels
                this.onChosen = onChosen
            }
        }
    }
}
