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
import com.example.traders.profile.adapters.PortfolioListAdapter
import com.example.traders.profile.cryptoData.CryptoInUsd
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioFragment: BaseFragment() {
    val viewModel: PortfolioViewModel by viewModels()
    private lateinit var adapter: PortfolioListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        binding.setUpPieChart()
        binding.setUpClickListeners()
        binding.setUpAdapter()

        // Update portfolio on list change
        viewModel.livePortfolioList.observe(this) {
            it?.let {
                viewModel.updateStateData()
                binding.updateMessageVisibility(it)
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                binding.updateUiData(it)
            }
        }
        return binding.root
    }

    private fun FragmentPortfolioBinding.setUpPieChart() {
        secondPiechart.setUsePercentValues(true)
        secondPiechart.setUsePercentValues(true)
        secondPiechart.setCenterTextSize(20F)
        secondPiechart.setDrawEntryLabels(false)
        secondPiechart.description.isEnabled = false
        secondPiechart.legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        secondPiechart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        secondPiechart.legend.orientation = Legend.LegendOrientation.VERTICAL
    }

    private fun FragmentPortfolioBinding.updateUiData(state: PortfolioState) {
        if(state.chartReadyForUpdate) {
            val pieDataSet = PieDataSet(state.chartData, "Portfolio")
            pieDataSet.setColors(state.colors)
            secondPiechart.data = PieData(pieDataSet)
            secondPiechart.invalidate()
            secondPiechart.animate()
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
        adapter = PortfolioListAdapter()
        portfolioList.adapter = adapter
    }

    private fun FragmentPortfolioBinding.updateMessageVisibility(list: List<Crypto>) {
        emptyListMessage.visibility = when(list) {
            emptyList<Crypto>() -> View.VISIBLE
            else -> View.GONE
        }
    }
}

