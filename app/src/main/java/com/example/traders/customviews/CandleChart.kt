package com.example.traders.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.traders.R
import com.example.traders.roundNumber

class CandleChart(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var cryptoData: List<List<Float>> = emptyList()
    private var linePositions: MutableList<List<Float>>   = mutableListOf()
    private var candlePositions: MutableList<List<Float>> = mutableListOf()
    private var mWidth = 0f
    private var mHeight = 0f
    private var candleWidth = 0f
    private var candleSpacing = 0f
    private var chartSizeMultiplier = 0.84f
    private var mGreenPaint = Paint()
    private var mRedPaint = Paint()
    private var mGreenLinePaint = Paint()
    private var mRedLinePaint = Paint()
    private var mTextPaint = Paint()
    private var minVal = 0f
    private var maxVal = 0f
    private var maxValPosition = 0f
    private var minValPosition = 0f
    private var currentPricePosition = 0f


    fun importListValues(list: List<List<Float>>) {
        //listof([volume, open, high, low, close], [], ..., [])
        Log.e("CryptoChart", "importListValues called")
        cryptoData = list
        minVal = list.minOf { it[3] }
        maxVal = list.maxOf { it[2] }
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
        mTextPaint.textSize = 25F
        mTextPaint.strokeWidth = 3F
        mGreenPaint.strokeWidth = candleWidth
        mRedPaint.strokeWidth = candleWidth
        mGreenLinePaint.strokeWidth = 3f
        mRedLinePaint.strokeWidth = 3f
    }

    private fun calculateCoords() {
        var xVal: Float = mWidth * (1 - chartSizeMultiplier) / 4 // assign start position of X
        var yLineTop: Float
        var yLineBottom: Float
        var yCandleOpen: Float
        var yCandleClose: Float
        linePositions.clear()
        candlePositions.clear()

        val minMaxDiff = maxVal - minVal

        for(i in 0..(cryptoData.size -1)) {
            yLineTop = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][2] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            yLineBottom = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][3] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            linePositions.add(listOf(yLineTop, yLineBottom, xVal))
            if(cryptoData[i][2] == maxVal) maxValPosition = yLineTop
            if(cryptoData[i][3] == minVal) minValPosition = yLineBottom

            yCandleOpen = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][1] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            yCandleClose = mHeight - ((mHeight * 0.9f) * ((cryptoData[i][4] - minVal)/minMaxDiff) + (mHeight * 0.05f))
            candlePositions.add(listOf(yCandleOpen, yCandleClose, xVal))
            if(i == (cryptoData.size-1)) currentPricePosition = yCandleClose
            xVal = xVal + (candleSpacing) + candleWidth
        }
    }

    private fun drawCandles( canvas: Canvas? ) {
        for(i in 0..linePositions.size - 1) {
            // height is compared inversely duo to top position of height is 0
            if(candlePositions[i][1] < candlePositions[i][0]) {
                // draw line
                canvas?.drawLine(linePositions[i][2], linePositions[i][0], linePositions[i][2], linePositions[i][1], mGreenLinePaint)
                canvas?.drawLine(candlePositions[i][2], candlePositions[i][0], candlePositions[i][2], candlePositions[i][1], mGreenPaint)

            } else {
                canvas?.drawLine(linePositions[i][2], linePositions[i][0], linePositions[i][2], linePositions[i][1], mRedLinePaint)
                canvas?.drawLine(candlePositions[i][2], candlePositions[i][0], candlePositions[i][2], candlePositions[i][1], mRedPaint)
            }
            // Drawing high, low, current lines
            if(linePositions[i][0] == maxValPosition){
                canvas?.drawLine(linePositions[i][2],linePositions[i][0], mWidth, linePositions[i][0], mGreenLinePaint)
                mTextPaint.color = ResourcesCompat.getColor(getResources(), R.color.green, null);
                canvas?.drawText(roundNumber(cryptoData[i][2].toDouble()), mWidth - 115F, linePositions[i][0] - 5F, mTextPaint)
            }
            if(linePositions[i][1] == minValPosition) {
                canvas?.drawLine(linePositions[i][2],linePositions[i][1], mWidth, linePositions[i][1], mRedLinePaint)
                mTextPaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);
                canvas?.drawText(roundNumber(cryptoData[i][3].toDouble()), mWidth - 115F, linePositions[i][1] - 5F, mTextPaint)
            }
        }

        // Draw most recent price
        mTextPaint.color = ResourcesCompat.getColor(getResources(), R.color.light_gray, null);
        canvas?.drawLine(candlePositions.last()[2],candlePositions.last()[1], mWidth, candlePositions.last()[1], mTextPaint)
        canvas?.drawText(roundNumber(cryptoData.last()[4].toDouble()), mWidth - 115F, candlePositions.last()[1] - 5F, mTextPaint)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        Toast.makeText(context, "this works", Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        canvas?.drawColor(Color.WHITE)
        if(cryptoData.size > 0) drawCandles(canvas)
    }
}