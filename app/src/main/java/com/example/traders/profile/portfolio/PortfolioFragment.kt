package com.example.traders.profile.portfolio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentPortfolioBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PortfolioFragment: BaseFragment() {
    val viewModel: PortfolioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val parentFragment = parentFragment
        Log.e("TAG", "PARENT FRAGMENT ${parentFragment}")
        val binding = FragmentPortfolioBinding.inflate(inflater, container, false)
        binding.setUpPieChart()
        lifecycleScope.launch {
            viewModel.state.collect {
                binding.updateData(it)
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

    private fun FragmentPortfolioBinding.updateData(state: PortfolioState) {
        if(state.chartReadyForUpdate) {
            val pieDataSet = PieDataSet(state.chartData, "Portfolio")
            pieDataSet.setColors(state.colors)
            secondPiechart.data = PieData(pieDataSet)
            secondPiechart.invalidate()
            secondPiechart.animate()
            viewModel.chartUpdated()
        }

        state.portfolioInUsd?.let {
            totalBalance.text = totalBalance.context.getString(
                R.string.usd_sign,
                state.portfolioInUsd.toString()
            )
        }
    }

}

