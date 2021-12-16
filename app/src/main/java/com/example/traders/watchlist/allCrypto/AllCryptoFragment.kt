package com.example.traders.watchlist.allCrypto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.traders.databinding.FragmentTabAllCryptoBinding
import com.example.traders.watchlist.WatchListFragmentDirections
import com.example.traders.watchlist.adapters.SingleCryptoListener
import com.example.traders.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCryptoFragment : Fragment() {
    private val viewModel: AllCryptoViewModel by viewModels()
    private var adapter: WatchListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTabAllCryptoBinding.inflate(inflater, container, false)
        adapter = WatchListAdapter(SingleCryptoListener{ slug, symbol ->
            viewModel.onCryptoClicked(slug, symbol)
        })
        binding.itemsList.adapter = adapter

        viewModel.cryptoData.observe(viewLifecycleOwner, { response ->
            adapter?.updateData(response)
        })

        viewModel.cryptoValues.observe(viewLifecycleOwner, { list ->
            list?.let {
                this.findNavController().navigate(WatchListFragmentDirections
                    .actionWatchListFragmentToCryptoItem(list[0], list[1]))
            }
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