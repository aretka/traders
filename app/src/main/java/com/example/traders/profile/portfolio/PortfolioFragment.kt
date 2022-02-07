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
import com.example.traders.dialogs.depositDialog.DepositDialogFragment
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
        binding.setUpClickListeners()

        // Update portfolio on list change
        viewModel.livePortfolioList.observe(this) {
            it?.let {
                viewModel.updateData()
                Log.e("ROOM_LIVE_DATA", "Size: ${it.size}")
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
}

