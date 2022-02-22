package com.example.traders.profile.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.database.Crypto
import com.example.traders.databinding.FragmentPortfolioBinding
import com.example.traders.dialogs.depositDialog.DepositDialogFragment
import com.example.traders.profile.ProfileFragmentDirections
import com.example.traders.profile.adapters.PortfolioListAdapter
import com.example.traders.watchlist.WatchListFragmentDirections
import com.example.traders.watchlist.adapters.SingleCryptoListener
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioFragment: BaseFragment() {
    private lateinit var binding: FragmentPortfolioBinding
    val viewModel: PortfolioViewModel by viewModels()
    private lateinit var adapter: PortfolioListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        binding.setUpPieChart()
        binding.setUpClickListeners()
        binding.setUpAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Update portfolio on list change
        viewModel.livePortfolioList.observe(viewLifecycleOwner) {
            it?.let {
                binding.updateMessageVisibility(it)
                viewModel.updateStateData()
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                binding.updateUiData(it)
            }
        }
    }

    private fun FragmentPortfolioBinding.setUpPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.setUsePercentValues(true)
        pieChart.setCenterTextSize(20F)
        pieChart.setDrawEntryLabels(false)
        pieChart.description.isEnabled = false
        pieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        pieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
    }

    private fun FragmentPortfolioBinding.updateUiData(state: PortfolioState) {
        if(state.chartReadyForUpdate) {
            val pieDataSet = PieDataSet(state.chartData, "Portfolio")
            pieDataSet.setColors(state.colors)
            pieChart.data = PieData(pieDataSet)
            pieChart.invalidate()
            pieChart.animate()
            viewModel.chartUpdated()
            adapter.addHeaderAndSubmitList(state.cryptoListInUsd)
        }

        state.totalPortfolioBalance?.let {
            totalBalance.text = totalBalance.context.getString(
                R.string.usd_sign,
                state.totalPortfolioBalance.toString()
            )
        }
    }

    private fun FragmentPortfolioBinding.setUpClickListeners() {
        depositBtn.setOnClickListener {
            openDialog()
        }
        withdrawBtn.setOnClickListener {
            viewModel.deleteAllDbRows()
        }
    }

    private fun openDialog() {
        val depositDialog = DepositDialogFragment()
        depositDialog.show(parentFragmentManager, "deposit_dialog")
    }

    private fun FragmentPortfolioBinding.setUpAdapter() {
        adapter = PortfolioListAdapter(SingleCryptoListener { slug, symbol ->
            if (symbol != null) {
                val direction = ProfileFragmentDirections
                    .actionUserProfileFragmentToCryptoItemFragment(slug, symbol)
                navController.navigate(direction)
            }
        })
        portfolioList.adapter = adapter
    }

    private fun FragmentPortfolioBinding.updateMessageVisibility(list: List<Crypto>) {
        emptyListMessage.visibility = when(list) {
            emptyList<Crypto>() -> View.VISIBLE
            else -> View.GONE
        }
    }
}

