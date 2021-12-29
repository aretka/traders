package com.example.traders.watchlist.allCrypto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.traders.databinding.FragmentTabAllCryptoBinding
import com.example.traders.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCryptoFragment : Fragment() {
    private val viewModel: AllCryptoViewModel by viewModels()
    private var adapter: WatchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabAllCryptoBinding.inflate(inflater, container, false)
//        viewModel.addItemsToList()
        adapter = WatchListAdapter()
        binding.itemsList.adapter = adapter

        viewModel.cryptoData.observe(viewLifecycleOwner, { response ->
            Log.d("Response", response[0].id)
            response.forEach { crypto ->
                Log.d(
                    "Response",
                    crypto.symbol + " - Open: " +
                            crypto.metrics.market_data.ohlcv_last_24_hour.open.toString() + " Close: " +
                            crypto.metrics.market_data.ohlcv_last_24_hour.close.toString()
                )
            }
            adapter?.updateData(response)
        })

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        lifecycleScope.launch {
//            viewModel.state.collect { state ->
//                adapter?.updateData(state.cryptoList)
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
    }
}