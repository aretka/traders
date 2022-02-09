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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment: BaseFragment() {

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        viewModel.transactionList.observe(this) {
            it?.let { list ->
                binding.emptyListMessageVisibility(it)
                if(list.isNotEmpty()) Log.e("TRANSACTION_INFO", "${list.last().symbol} ${list.last().time}")
            }
        }

        return binding.root
    }

    private fun FragmentHistoryBinding.emptyListMessageVisibility(list: List<Transaction>) {
        emptyTransactionsListMessage.visibility = when(list) {
            emptyList<Transaction>() -> View.VISIBLE
            else -> View.GONE
        }
    }
}
