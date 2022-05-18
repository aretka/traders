package com.example.traders.presentation.customviews

interface ScrubListener {
    fun onScrubbed(x: Float, y: Float)
    fun onScrubEnded()
}