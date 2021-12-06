package com.example.traders.watchlist.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.traders.watchlist.allCrypto.AllCryptoFragment
import com.example.traders.watchlist.favourites.FavouriteCryptoFragment
import com.example.traders.watchlist.newCrypto.NewCryptoFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :  FragmentStateAdapter(fragmentManager, lifecycle){
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                AllCryptoFragment()
            }
            1 -> {
                FavouriteCryptoFragment()
            }
            2 -> {
                NewCryptoFragment()
            }
            else->{
                Fragment()
            }
        }
    }

}