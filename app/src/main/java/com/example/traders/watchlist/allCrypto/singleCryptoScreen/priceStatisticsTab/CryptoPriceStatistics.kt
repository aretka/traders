package com.example.traders.watchlist.allCrypto.singleCryptoScreen.priceStatisticsTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentCryptoItemPriceStatisticsBinding
import com.example.traders.roundNumber
import com.example.traders.watchlist.cryptoData.cryptoStatsData.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CryptoPriceStatistics(val symbol: String) : BaseFragment() {

    private val viewModel: CryptoPriceStatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCryptoItemPriceStatisticsBinding.inflate(inflater, container, false)
        viewModel.fetchCryptoPriceStatistics(symbol)
        viewModel.cryptoStatsResponse.observe(viewLifecycleOwner, {
            fillDescriptionData(binding.mainCryptoDescription, it)
            fillLast1Hour(
                binding.last1hourOpen,
                binding.last1hourHigh,
                binding.last1hourLow,
                binding.last1hourClose,
                binding.last1hourVolume,
                it.data.market_data.ohlcv_last_1_hour
            )
            fillLast24Hour(
                binding.last24hourOpen,
                binding.last24hourHigh,
                binding.last24hourLow,
                binding.last24hourClose,
                binding.last24hourVolume,
                it.data.market_data.ohlcv_last_24_hour
            )
            fillATH(
                binding.ATHPrice,
                binding.ATHDate,
                binding.ATHDaysSince,
                binding.ATHPercentDown,
                binding.ATHBreakEvenMultiple,
                it.data.all_time_high
            )
            fillRoiData(
                binding.roiDataLast1Week,
                binding.roiDataLast1Month,
                binding.roiDataLast3Months,
                binding.roiDataLast1Year,
                it.data.roi_data
            )
        })

        binding.expand1HourIcon.setOnClickListener { onIconClicked(it, binding.layout1Hour) }

        binding.expand24HourIcon.setOnClickListener { onIconClicked(it, binding.layout24Hour) }

        binding.expandATHIcon.setOnClickListener { onIconClicked(it, binding.layoutATH) }

        binding.expandRoiDataIcon.setOnClickListener { onIconClicked(it, binding.layoutRoiData) }

        return binding.root
    }

    private fun fillRoiData(
        roiDataLast1Week: TextView,
        roiDataLast1Month: TextView,
        roiDataLast3Months: TextView,
        roiDataLast1Year: TextView,
        roiData: RoiData
    ) {
        roiDataLast1Week.text = roundNumber(roiData.percent_change_last_1_week)
        roiDataLast1Month.text = roundNumber(roiData.percent_change_last_1_month)
        roiDataLast3Months.text = roundNumber(roiData.percent_change_last_3_months)
        roiDataLast1Year.text = roundNumber(roiData.percent_change_last_1_year)
    }

    private fun fillATH(
        athPrice: TextView,
        athDate: TextView,
        athDaysSince: TextView,
        athPercentDown: TextView,
        athBreakEvenMultiple: TextView,
        athData: AllTimeHigh
    ) {
        athPrice.text = roundNumber(athData.price) + '$'
        athDate.text = athData.at
        athDaysSince.text = athData.days_since.toString()
        athPercentDown.text = roundNumber(athData.percent_down) + '%'
        athBreakEvenMultiple.text = roundNumber(athData.breakeven_multiple)
    }

    private fun fillLast24Hour(
        last24hourOpen: TextView,
        last24hourHigh: TextView,
        last24hourLow: TextView,
        last24hourClose: TextView,
        last24hourVolume: TextView,
        cryptoStats: OhlcvLast24Hour
    ) {
        last24hourOpen.text = roundNumber(cryptoStats.open) + '$'
        last24hourHigh.text = roundNumber(cryptoStats.high) + '$'
        last24hourLow.text = roundNumber(cryptoStats.low) + '$'
        last24hourClose.text = roundNumber(cryptoStats.close) + '$'
        last24hourVolume.text = roundNumber(cryptoStats.volume) + '$'
    }

    private fun fillDescriptionData(textView: TextView, cryptoStats: CryptoStatistics) {
        textView.text = textView.context.getString(
            R.string.crypto_description,
            cryptoStats.data.slug,
            cryptoStats.data.market_data.price_usd,
            cryptoStats.data.market_data.ohlcv_last_24_hour.volume,
            cryptoStats.data.marketcap.marketcap_dominance_percent,
            cryptoStats.data.marketcap.current_marketcap_usd,
            cryptoStats.data.supply.circulating
        )
    }

    private fun fillLast1Hour(
        last1hourOpen: TextView,
        last1hourHigh: TextView,
        last1hourLow: TextView,
        last1hourClose: TextView,
        last1hourVolume: TextView,
        cryptoStats: OhlcvLast1Hour
    ) {
        last1hourOpen.text = roundNumber(cryptoStats.open) + '$'
        last1hourHigh.text = roundNumber(cryptoStats.high) + '$'
        last1hourLow.text = roundNumber(cryptoStats.low) + '$'
        last1hourClose.text = roundNumber(cryptoStats.close) + '$'
        last1hourVolume.text = roundNumber(cryptoStats.volume) + '$'
    }

    private fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else Math.max(
                        1,
                        (targetHeight * interpolatedTime).toInt()
                    )
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.setDuration(
            (targetHeight / v.getContext().getResources().getDisplayMetrics().density).toLong() * 2
        )
        v.startAnimation(a)
    }

    private fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.setDuration(
            (initialHeight / v.getContext().getResources().getDisplayMetrics().density).toLong() * 2
        )
        v.startAnimation(a)
    }

    private fun onIconClicked(icon: View, expandableLayout: LinearLayout) {
        if (expandableLayout.visibility == View.GONE) {
            expand(expandableLayout)
            icon.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
        } else {
            collapse(expandableLayout)
            icon.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
        }
    }
}



