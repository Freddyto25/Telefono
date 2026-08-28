package com.lz.telefonoprolz.ui.recents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lz.telefonoprolz.data.CallLogRepository
import com.lz.telefonoprolz.databinding.FragmentRecentsBinding

class RecentsFragment : Fragment() {

    private var _binding: FragmentRecentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RecentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RecentsAdapter { item ->
            ContactActionsBottomSheet.newInstance(item.displayName ?: item.number, item.number)
                .show(childFragmentManager, "contact_actions")
        }
        binding.recyclerRecents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecents.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { loadCalls() }
        loadCalls()
    }

    override fun onResume() {
        super.onResume()
        loadCalls()
    }

    private fun loadCalls() {
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.READ_CALL_LOG
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            binding.swipeRefresh.isRefreshing = false
            return
        }
        val calls = CallLogRepository.getRecentCalls(requireContext())
        adapter.submitList(calls)
        binding.textEmptyRecents.visibility = if (calls.isEmpty()) View.VISIBLE else View.GONE
        binding.swipeRefresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
