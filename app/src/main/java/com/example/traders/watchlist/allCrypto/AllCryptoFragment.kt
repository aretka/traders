package com.example.traders.watchlist.allCrypto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.BaseFragment
import com.example.traders.databinding.FragmentTabAllCryptoBinding
import com.example.traders.watchlist.WatchListFragmentDirections
import com.example.traders.watchlist.adapters.SingleCryptoListener
import com.example.traders.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllCryptoFragment : BaseFragment() {

    private val viewModel: AllCryptoViewModel by viewModels()
    private var adapter: WatchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTabAllCryptoBinding.inflate(inflater, container, false)

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

    private fun FragmentTabAllCryptoBinding.setPullToRefreshListener() {
        pullToRefresh.setOnRefreshListener {
            viewModel.getCryptoOnRefresh()
        }
    }

    private fun FragmentTabAllCryptoBinding.setListAdapter() {
        adapter = WatchListAdapter(SingleCryptoListener { slug, symbol ->
            if (symbol != null) {
                val direction = WatchListFragmentDirections
                    .actionWatchListFragmentToCryptoItem(slug, symbol)
                navController.navigate(direction)
            }
        })
        itemsList.adapter = adapter
    }

    private fun FragmentTabAllCryptoBinding.handleLoader(isLoading: Boolean) {
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
