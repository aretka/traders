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
/*
* TODO: viewModel read all favourite list and update list
*
* */
@AndroidEntryPoint
class WatchListFragment : BaseFragment() {

    private val viewModel: WatchListViewModel by viewModels()
    private lateinit var binding: FragmentWatchListBinding
    private var adapter: WatchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchListBinding.inflate(layoutInflater, container, false)

        binding.setPullToRefreshListener()
        binding.setListAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                binding.changeLoaderVisibility(it)
            }
            lifecycleScope.launch {
                state.collect { state ->
                    binding.pullToRefresh.isRefreshing = state.isRefreshing
                    adapter?.submitList(state.binanceCryptoData)
                }
            }
        }
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
                    .actionWatchListFragmentToCryptoItem(slug, symbol, false)
                navController.navigate(direction)
            }
        })
        itemsList.adapter = adapter
    }

    private fun FragmentWatchListBinding.changeLoaderVisibility(isLoading: Boolean) {
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
