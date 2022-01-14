package com.example.traders.watchlist.allCrypto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.traders.BaseFragment
import com.example.traders.databinding.FragmentTabAllCryptoBinding
import com.example.traders.watchlist.adapters.SingleCryptoListener
import com.example.traders.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint

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
            cryptoData.observe(viewLifecycleOwner) {
                adapter?.updateData(it.cryptoList)
            }
            isLoading.observe(viewLifecycleOwner) { isLoading ->
                binding.handleLoader(isLoading)
            }
            viewModel.isRefreshing.observe(viewLifecycleOwner) {
                binding.pullToRefresh.isRefreshing = it
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
        adapter = WatchListAdapter(SingleCryptoListener { id, symbol ->
            viewModel.onCryptoClicked(id, symbol)
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
