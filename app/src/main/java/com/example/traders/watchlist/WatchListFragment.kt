package com.example.traders.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentWatchListBinding
import com.example.traders.watchlist.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WatchListFragment : BaseFragment() {

    private val viewModel: WatchListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.errorEvent.collect { error ->
                showError(error.e.message.orEmpty())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWatchListBinding.inflate(layoutInflater, container, false)
        setUpTabs(binding.cryptoPageViewer, binding.cryptoTab)

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpTabs(viewPager: ViewPager2, tabLayout: TabLayout) {
        val viewPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.watchlist_tab_all_crypto_label)
                1 -> tab.text = getString(R.string.watchlist_tab_favourites_label)
                2 -> tab.text = getString(R.string.watchlist_tab_new_crypto_label)
            }
        }.attach()
    }
}
