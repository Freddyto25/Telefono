package com.lz.telefonoprolz.ui.recents

import android.provider.CallLog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lz.telefonoprolz.R
import com.lz.telefonoprolz.data.RecentCallItem
import com.lz.telefonoprolz.databinding.ItemRecentCallBinding
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class RecentsAdapter(
    private val onItemClick: (RecentCallItem) -> Unit
) : RecyclerView.Adapter<RecentsAdapter.ViewHolder>() {

    private var items: List<RecentCallItem> = emptyList()

    fun submitList(newItems: List<RecentCallItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentCallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemRecentCallBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentCallItem) {
            val context = binding.root.context
            val name = item.displayName ?: item.number
            binding.textName.text = name

            val typeLabel = when (item.type) {
                CallLog.Calls.INCOMING_TYPE -> context.getString(R.string.call_type_incoming)
                CallLog.Calls.OUTGOING_TYPE -> context.getString(R.string.call_type_outgoing)
                CallLog.Calls.MISSED_TYPE -> context.getString(R.string.call_type_missed)
                CallLog.Calls.REJECTED_TYPE -> context.getString(R.string.call_type_rejected)
                else -> ""
            }
            val dateText = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(Date(item.date))
            binding.textDetail.text = "$typeLabel · $dateText"

            binding.root.contentDescription = context.getString(
                R.string.desc_recent_item, name, typeLabel, dateText
            )

            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnQuickCall.setOnClickListener {
                CallLauncher.call(context, item.number)
            }
        }
    }
}
