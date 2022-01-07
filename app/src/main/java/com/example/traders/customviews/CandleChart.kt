package com.example.traders.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.traders.R

class CandleChart(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var cryptoData: List<List<Float>> = emptyList()
    private var linePositions: MutableList<List<Float>>   = mutableListOf()
    private var candlePositions: MutableList<List<Float>> = mutableListOf()
    private var mWidth = 0f
    private var mHeight = 0f
    private var candleWidth = 0f
    private var candleSpacing = 0f
    private var chartSizeMultiplier = 0.9f
    private var mGreenPaint = Paint()
    private var mRedPaint = Paint()
    private var mGreenLinePaint = Paint()
    private var mRedLinePaint = Paint()
    private var minVal = 0f
    private var maxVal = 0f


    fun importListValues(list: List<List<Float>>, min: Float, max: Float) {
        Log.e("CryptoChart", "importListValues called")
        cryptoData = list
        minVal = min
        maxVal = max
        calculateVals()
    }

    fun calculateVals() {
        Log.e("CryptoChart", "calculateVals called")
        calculateSizeVals()
        calculatePaintVals()
        calculateCoords()
    }

    private fun calculateSizeVals() {
        mWidth = width.toFloat()
        mHeight = height.toFloat()
        candleWidth = mWidth * chartSizeMultiplier / (cryptoData.size-1) * 0.6f
        candleSpacing = mWidth * chartSizeMultiplier / (cryptoData.size-1) * 0.4f
    }

    private fun calculatePaintVals() {
        mGreenPaint.color = ResourcesCompat.getColor(getResources(), R.color.green, null); //without theme
        mRedPaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);
        mGreenLinePaint.color = ResourcesCompat.getColor(getResources(), R.color.green, null);
        mRedLinePaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);

        mGreenPaint.strokeWidth = candleWidth
        mRedPaint.strokeWidth = candleWidth
        mGreenLinePaint.strokeWidth = 3f
        mRedLinePaint.strokeWidth = 3f
    }

    private fun calculateCoords() {
        var xVal: Float = mWidth * (1 - chartSizeMultiplier) / 2 // assign start position of X
        var yLineTop: Float
        var yLineBottom: Float
        var yCandleTop: Float
        var yCandleBottom: Float
        linePositions.clear()
        candlePositions.clear()

        val minMaxDiff = maxVal - minVal

        for(i in 0..(cryptoData.size - 1)) {
            yLineTop = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][2] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            yLineBottom = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][3] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            linePositions.add(listOf(yLineTop, yLineBottom, xVal))

            yCandleTop = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][1] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            yCandleBottom = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][4] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            candlePositions.add(listOf(yCandleTop, yCandleBottom, xVal))

            xVal = xVal + (candleSpacing) + candleWidth
        }
    }

    private fun drawCandles(
        canvas: Canvas?,
        greenLinePaint: Paint,
        redLinePaint: Paint,
        greenPaint: Paint,
        redPaint: Paint
    ) {
        for(i in 0..linePositions.size - 1) {
            // height is compared inversely duo to top position of height is 0
            if(candlePositions[i][1] < candlePositions[i][0]) {
                canvas?.drawLine(linePositions[i][2], linePositions[i][0], linePositions[i][2], linePositions[i][1], greenLinePaint)
                canvas?.drawLine(candlePositions[i][2], candlePositions[i][0], candlePositions[i][2], candlePositions[i][1], greenPaint)
            } else {
                canvas?.drawLine(linePositions[i][2], linePositions[i][0], linePositions[i][2], linePositions[i][1], redLinePaint)
                canvas?.drawLine(candlePositions[i][2], candlePositions[i][0], candlePositions[i][2], candlePositions[i][1], redPaint)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        canvas?.drawColor(Color.WHITE)
        if(cryptoData.size > 0) drawCandles(canvas, mGreenLinePaint, mRedLinePaint, mGreenPaint, mRedPaint)
    }
}