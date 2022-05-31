package com.example.traders.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.presentation.BaseFragment
import com.example.traders.R
import com.example.traders.database.SortOrder
import com.example.traders.databinding.FragmentWatchListBinding
import com.example.traders.presentation.watchlist.adapters.SingleCryptoListener
import com.example.traders.presentation.watchlist.adapters.WatchListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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

        binding.setListAdapter()
        binding.setUpListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
//            TODO: Is naming is ok?
            onViewCreatedCalled()
            isLoading.observe(viewLifecycleOwner) { isVisible ->
                binding.changeLoaderVisibility(isVisible)
            }
            lifecycleScope.launchWhenStarted {
                state.collect { state ->
                    binding.updateUi(state)
                    adapter?.submitList(state.binanceCryptoData)
                }
            }
        }
    }

    private fun FragmentWatchListBinding.setListAdapter() {
        adapter = WatchListAdapter(SingleCryptoListener { slug, symbol ->
            val direction = WatchListFragmentDirections
                .actionWatchListFragmentToCryptoItem(slug, symbol)
            navController.navigate(direction)
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

    private fun FragmentWatchListBinding.updateUi(state: WatchListState) {
        pullToRefresh.isRefreshing = state.isRefreshing

        if(state.showFavourites) {
            favouriteBtn.setImageResource(R.drawable.ic_star_active)
        } else {
            favouriteBtn.setImageResource(R.drawable.ic_star_inactive)
        }

        this.setSortBtnStyleToDefault()
        when(state.sortOrder) {
            SortOrder.BY_NAME_DESC -> sortByNameDesc.setImageResource(R.drawable.ic_arrow_up_active)
            SortOrder.BY_NAME_ASC -> sortByNameAsc.setImageResource(R.drawable.ic_arrow_down_active)
            SortOrder.BY_CHANGE_DESC -> sortByChangeDesc.setImageResource(R.drawable.ic_arrow_up_active)
            SortOrder.BY_CHANGE_ASC -> sortByChangeAsc.setImageResource(R.drawable.ic_arrow_down_active)
            else -> return
        }
    }

    private fun FragmentWatchListBinding.setSortBtnStyleToDefault() {
        sortByNameDesc.setImageResource(R.drawable.ic_arrow_up_inactive)
        sortByNameAsc.setImageResource(R.drawable.ic_arrow_down_inactive)
        sortByChangeDesc.setImageResource(R.drawable.ic_arrow_up_inactive)
        sortByChangeAsc.setImageResource(R.drawable.ic_arrow_down_inactive)
    }

    private fun FragmentWatchListBinding.setUpListeners() {
        pullToRefresh.setOnRefreshListener {
            viewModel.getCryptoOnRefresh()
        }

        favouriteBtn.setOnClickListener {
            viewModel.onFavouriteButtonClicked()
        }

        sortByNameButtons.setOnClickListener {
            viewModel.onSortNameButtonClicked()
        }

        sortByChangeButtons.setOnClickListener {
            viewModel.onSortPriceChangeButtonClicked()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onFragmentStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter = null
    }
}
