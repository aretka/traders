package com.example.traders.profile.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.traders.BaseFragment
import com.example.traders.database.Transaction
import com.example.traders.databinding.FragmentHistoryBinding
import com.example.traders.profile.adapters.HistoryListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment() {

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: HistoryListAdapter
    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.setUpAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.transactionList.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding.emptyListMessageVisibility(it)
                adapter.addHeaderAndSubmitList(list.reversed())
            }
        }
    }

    private fun FragmentHistoryBinding.emptyListMessageVisibility(list: List<Transaction>) {
        emptyTransactionsListMessage.visibility = when (list) {
            emptyList<Transaction>() -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun FragmentHistoryBinding.setUpAdapter() {
        adapter = HistoryListAdapter({ viewModel.clearHistory() })
        historyList.adapter = adapter
    }
}
