package com.example.traders.watchlist.newCrypto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.traders.R
import com.example.traders.databinding.FragmentTabNewCryptoBinding

class NewCryptoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabNewCryptoBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
}