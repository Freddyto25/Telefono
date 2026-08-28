package com.lz.telefonoprolz.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lz.telefonoprolz.call.CallLauncher
import com.lz.telefonoprolz.data.ContactItem
import com.lz.telefonoprolz.databinding.ItemContactBinding

class ContactsAdapter(
    private val onItemClick: (ContactItem) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var items: List<ContactItem> = emptyList()

    fun submitList(newItems: List<ContactItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactItem) {
            binding.textContactName.text = item.name
            binding.root.contentDescription = item.name
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnQuickCallContact.setOnClickListener {
                CallLauncher.call(binding.root.context, item.number)
            }
        }
    }
}
