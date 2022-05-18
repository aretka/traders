package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import com.robinhood.spark.SparkAdapter

class LineChartAdapter(private var yData: List<Float>): SparkAdapter() {

    fun updateData(newList: List<Float>) {
       yData = newList
    }

    override fun getCount(): Int {
        return yData.size
    }

    override fun getItem(index: Int): Any {
        return yData[index]
    }

    override fun getY(index: Int): Float {
        return yData[index]
    }
}