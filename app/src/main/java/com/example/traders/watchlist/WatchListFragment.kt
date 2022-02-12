package com.example.traders.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.BaseFragment
import com.example.traders.databinding.FragmentWatchListBinding
import com.example.traders.watchlist.adapters.SingleCryptoListener
import com.example.traders.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WatchListFragment : BaseFragment() {

    private val viewModel: WatchListViewModel by viewModels()
    private var adapter: WatchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWatchListBinding.inflate(layoutInflater, container, false)
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                binding.handleLoader(it)
            }
            lifecycleScope.launch {
                state.collect { state ->
                    binding.pullToRefresh.isRefreshing = state.isRefreshing
                    adapter?.submitList(state.binanceCryptoData)
                }
            }
        }

        binding.setPullToRefreshListener()
        binding.setListAdapter()

        return binding.root
    }

    private fun FragmentWatchListBinding.setPullToRefreshListener() {
        pullToRefresh.setOnRefreshListener {
            viewModel.getCryptoOnRefresh()
        }
    }

    private fun FragmentWatchListBinding.setListAdapter() {
        adapter = WatchListAdapter(SingleCryptoListener { slug, symbol ->
            if (symbol != null) {
                val direction = WatchListFragmentDirections
                    .actionWatchListFragmentToCryptoItem(slug, symbol)
                navController.navigate(direction)
            }
        })
        itemsList.adapter = adapter
    }

    private fun FragmentWatchListBinding.handleLoader(isLoading: Boolean) {
        if (isLoading) {
            allCryptoLoader.visibility = View.VISIBLE
        } else {
            allCryptoLoader.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
    }
}
