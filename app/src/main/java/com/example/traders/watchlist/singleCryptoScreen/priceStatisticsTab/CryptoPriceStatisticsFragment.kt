package com.example.traders.watchlist.singleCryptoScreen.priceStatisticsTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.traders.*
import com.example.traders.databinding.FragmentCryptoItemPriceStatisticsBinding
import com.example.traders.dialogs.buyDialog.BuyDialogViewModel
import com.example.traders.utils.roundAndFormatDouble
import com.example.traders.utils.setPriceChangeText
import com.example.traders.utils.setPriceChangeTextColor
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.cryptoStatsData.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CryptoPriceStatisticsFragment(val crypto: FixedCryptoList) : BaseFragment() {

    @Inject
    lateinit var viewModelAssistedFactory: CryptoPriceStatisticsViewModel.Factory

    private val viewModel: CryptoPriceStatisticsViewModel by viewModels() {
        CryptoPriceStatisticsViewModel.provideFactory(viewModelAssistedFactory, crypto)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCryptoItemPriceStatisticsBinding.inflate(inflater, container, false)
        viewModel.cryptoStatsResponse.observe(viewLifecycleOwner, {
            fillMainSectionData(
                binding,
                it.data
            )

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

    private fun fillMainSectionData(binding: FragmentCryptoItemPriceStatisticsBinding, data: Data) {
        val priceChange =
            roundAndFormatDouble(data.market_data.ohlcv_last_24_hour.open - data.market_data.ohlcv_last_24_hour.close)
        binding.cryptoPrice.text = "$ ${roundAndFormatDouble(data.market_data.price_usd)}"
        binding.cryptoPriceChange.setPriceChangeText(
            priceChange,
            roundAndFormatDouble(data.market_data.percent_change_usd_last_24_hours)
        )
        binding.cryptoPriceChange.setPriceChangeTextColor()
        binding.marketDominance.text = "${roundAndFormatDouble(data.marketcap.marketcap_dominance_percent)}%"
        binding.marketCap.text = "$ ${roundAndFormatDouble(data.marketcap.current_marketcap_usd)}"
        binding.volume1h.text = "$ ${roundAndFormatDouble(data.market_data.ohlcv_last_1_hour.volume)}"
        binding.volume24h.text = "$ ${roundAndFormatDouble(data.market_data.ohlcv_last_24_hour.volume)}"
        Glide.with(binding.cryptoImage)
            .load("https://cryptologos.cc/logos/${data.slug}-${data.symbol.lowercase()}-logo.png?v=014")
            .placeholder(R.drawable.ic_image_error)
            .error(R.drawable.ic_image_error)
            .into(binding.cryptoImage)
    }

    private fun fillRoiData(
        roiDataLast1Week: TextView,
        roiDataLast1Month: TextView,
        roiDataLast3Months: TextView,
        roiDataLast1Year: TextView,
        roiData: RoiData
    ) {
        roiDataLast1Week.text = roundAndFormatDouble(roiData.percent_change_last_1_week)
        roiDataLast1Month.text = roundAndFormatDouble(roiData.percent_change_last_1_month)
        roiDataLast3Months.text = roundAndFormatDouble(roiData.percent_change_last_3_months)
        roiDataLast1Year.text = roundAndFormatDouble(roiData.percent_change_last_1_year)
    }

    private fun fillATH(
        athPrice: TextView,
        athDate: TextView,
        athDaysSince: TextView,
        athPercentDown: TextView,
        athBreakEvenMultiple: TextView,
        athData: AllTimeHigh
    ) {
        athPrice.text = roundAndFormatDouble(athData.price) + '$'
        athDate.text = athData.at
        athDaysSince.text = athData.days_since.toString()
        athPercentDown.text = roundAndFormatDouble(athData.percent_down) + '%'
        athBreakEvenMultiple.text = roundAndFormatDouble(athData.breakeven_multiple)
    }

    private fun fillLast24Hour(
        last24hourOpen: TextView,
        last24hourHigh: TextView,
        last24hourLow: TextView,
        last24hourClose: TextView,
        last24hourVolume: TextView,
        cryptoStats: OhlcvLast24Hour
    ) {
        last24hourOpen.text = roundAndFormatDouble(cryptoStats.open) + '$'
        last24hourHigh.text = roundAndFormatDouble(cryptoStats.high) + '$'
        last24hourLow.text = roundAndFormatDouble(cryptoStats.low) + '$'
        last24hourClose.text = roundAndFormatDouble(cryptoStats.close) + '$'
        last24hourVolume.text = roundAndFormatDouble(cryptoStats.volume) + '$'
    }

    private fun fillLast1Hour(
        last1hourOpen: TextView,
        last1hourHigh: TextView,
        last1hourLow: TextView,
        last1hourClose: TextView,
        last1hourVolume: TextView,
        cryptoStats: OhlcvLast1Hour
    ) {
        last1hourOpen.text = roundAndFormatDouble(cryptoStats.open) + '$'
        last1hourHigh.text = roundAndFormatDouble(cryptoStats.high) + '$'
        last1hourLow.text = roundAndFormatDouble(cryptoStats.low) + '$'
        last1hourClose.text = roundAndFormatDouble(cryptoStats.close) + '$'
        last1hourVolume.text = roundAndFormatDouble(cryptoStats.volume) + '$'
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



