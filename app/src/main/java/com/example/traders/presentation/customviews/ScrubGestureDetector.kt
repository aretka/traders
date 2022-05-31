package com.example.traders.presentation.customviews

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import android.view.View

class ScrubGestureDetector(
    private val scrubListener: ScrubListener,
    private val handler: Handler,
    private val touchSlop: Float
) : View.OnTouchListener {
    private var downX: Float = 0f
    private var downY: Float = 0f

    private val longPressRunnable = Runnable {
        scrubListener.onScrubbed(downX, downY)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        val x = event!!.x
        val y = event.y

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // store the time to compute whether future events are 'long presses'
                downX = x
                downY = y
                handler.postDelayed(longPressRunnable, LONG_PRESS_TIMEOUT_MS)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                // calculate the elapsed time since the down event
                val timeDelta = (event.eventTime - event.downTime).toFloat()

                // if the user has intentionally long-pressed
                if (timeDelta >= LONG_PRESS_TIMEOUT_MS) {
                    handler.removeCallbacks(longPressRunnable)
                    scrubListener.onScrubbed(x, y)
                } else {
                    // if we moved before longpress, remove the callback if we exceeded the tap slop
                    val deltaX = x - downX
                    val deltaY = y - downY
//                    "Touch slop" refers to the distance in pixels a user's touch can wander before the gesture is interpreted as scrolling.
                    if (deltaX >= touchSlop || deltaY >= touchSlop) {
                        handler.removeCallbacks(longPressRunnable)
                        // We got a MOVE event that exceeded tap slop but before the long-press
                        // threshold, we don't care about this series of events anymore.
                        return false
                    }
                }
                true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable)
                scrubListener.onScrubEnded()
                true
            }
            else -> false
        }
    }

    companion object {
        private val LONG_PRESS_TIMEOUT_MS : Long = 250
    }

}