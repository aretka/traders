package com.example.traders.profile.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.traders.BaseFragment
import com.example.traders.databinding.FragmentPortfolioBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PortfolioFragment: BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPortfolioBinding.inflate(inflater, container, false)

        return binding.root
    }
}