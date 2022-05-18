package com.example.traders.presentation.customviews

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.traders.R
import com.example.traders.utils.roundAndFormatDouble
import java.util.Collections.binarySearch
import kotlin.math.abs

class CandleChart(context: Context, attrs: AttributeSet) : View(context, attrs), ScrubListener {
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

    // scrubLine
    val scrubLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val scrubLinePath = Path()
    // listeners
    private var scrubListener: OnScrubListener? = null
    lateinit var scrubGestureDetector: ScrubGestureDetector

    init {
        setPaintColors()
        setScrubLinePaint()
        setPaintSize()
        setScrubGestureDetector()
    }

    fun setScrubListener(listener: () -> Unit) {
        scrubListener = OnScrubListener(listener)
    }

    fun importListValues(list: List<List<Float>>) {
        //listof([volume, open, high, low, close], [], ..., [])
        if(list == cryptoData) return
        cryptoData = list
        minVal = list.minOf { it[3] }
        maxVal = list.maxOf { it[2] }
        setWidthAndHeigth()
        calculateCandleSizes()
        calculateCoords()
        invalidate()
    }

    private fun setPaintColors() {
        mGreenPaint.color = ResourcesCompat.getColor(getResources(), R.color.green, null); //without theme
        mRedPaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);
        mGreenLinePaint.color = ResourcesCompat.getColor(getResources(), R.color.green, null);
        mRedLinePaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);
    }

    private fun setScrubLinePaint() {
        scrubLinePaint.style = Paint.Style.STROKE
        scrubLinePaint.strokeWidth = 5f
        scrubLinePaint.color = ResourcesCompat.getColor(getResources(), R.color.black, null)
//        scrubLinePaint.strokeCap = Paint.Cap.ROUND
    }

    private fun setPaintSize() {
        mTextPaint.textSize = 25F
        mTextPaint.strokeWidth = 3F
        mGreenLinePaint.strokeWidth = 3f
        mRedLinePaint.strokeWidth = 3f
    }

    private fun setScrubGestureDetector() {
        val handler = Handler(Looper.getMainLooper())
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        scrubGestureDetector = ScrubGestureDetector(this, handler, touchSlop)
        scrubGestureDetector.enabled = true
        setOnTouchListener(scrubGestureDetector)
    }

    private fun setWidthAndHeigth() {
        mWidth = width.toFloat()
        mHeight = height.toFloat()
    }

    private fun calculateCandleSizes() {
        candleWidth = mWidth * chartSizeMultiplier / (cryptoData.size-1) * 0.6f
        candleSpacing = mWidth * chartSizeMultiplier / (cryptoData.size-1) * 0.4f
        mGreenPaint.strokeWidth = candleWidth
        mRedPaint.strokeWidth = candleWidth
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
            xVal += (candleSpacing) + candleWidth
        }
    }

    private fun drawCandles( canvas: Canvas? ) {
        if(cryptoData.size > 0) {
            Log.e("tag", "drawCandles", )
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
                    canvas?.drawText(roundAndFormatDouble(cryptoData[i][2].toDouble()), mWidth - 115F, linePositions[i][0] - 5F, mTextPaint)
                }
                if(linePositions[i][1] == minValPosition) {
                    canvas?.drawLine(linePositions[i][2],linePositions[i][1], mWidth, linePositions[i][1], mRedLinePaint)
                    mTextPaint.color = ResourcesCompat.getColor(getResources(), R.color.red, null);
                    canvas?.drawText(roundAndFormatDouble(cryptoData[i][3].toDouble()), mWidth - 115F, linePositions[i][1] - 5F, mTextPaint)
                }
            }

            // Draw most recent price
            mTextPaint.color = ResourcesCompat.getColor(getResources(), R.color.light_gray, null);
            canvas?.drawLine(candlePositions.last()[2],candlePositions.last()[1], mWidth, candlePositions.last()[1], mTextPaint)
            canvas?.drawText(roundAndFormatDouble(cryptoData.last()[4].toDouble()), mWidth - 115F, candlePositions.last()[1] - 5F, mTextPaint)
        }
    }

//    override fun setOnClickListener(l: OnClickListener?) {
//        Toast.makeText(context, "this works", Toast.LENGTH_SHORT).show()
//    }

    class OnScrubListener(private val clickListener: () -> Unit) {
        fun onScrubbed() = clickListener()
    }

    private fun setScrubLine(x: Float) {
        scrubLinePath.reset()
        scrubLinePath.moveTo(x, paddingTop.toFloat())
        scrubLinePath.lineTo(x, (height - paddingBottom).toFloat())
        invalidate()
    }

    private fun getNearestIndex(xPoints: List<Float>, x: Float): Int {
        val index = binarySearch(xPoints, x)

        // if index non negative it means that x matches exact point
        if(index >= 0) return index

        // if index closest to left return first element
        if(index == -1) return index + 1

        // if index closest to right return last element
        if(abs(index) == xPoints.size) return xPoints.size - 1

        // otherwise return element closest to the x coordinate
        val point1Index = abs(index+1)
        val point2Index = abs(index)
        val distance1 = abs(x - xPoints[point1Index])
        val distance2 = abs(x - xPoints[point2Index])
        return if(distance1 <= distance2) {
             point1Index
        } else {
             point2Index
        }
    }

    override fun onScrubbed(x: Float, y: Float) {
//        if (adapter == null || adapter.getCount() == 0) return
        parent.requestDisallowInterceptTouchEvent(true)
        val xPoints = linePositions.map { it.last() }
        val index = getNearestIndex(xPoints, x)
        if (scrubListener != null) {
            scrubListener!!.onScrubbed()
        }
        setScrubLine(xPoints[index])
    }

    override fun onScrubEnded() {
        scrubLinePath.reset()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        canvas?.drawColor(Color.WHITE)
        drawCandles(canvas)
        canvas?.drawPath(scrubLinePath, scrubLinePaint)
    }
}