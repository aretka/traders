package com.example.traders.singleCryptoScreen

import android.os.Bundle
import android.text.style.TabStopSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.traders.BaseFragment
import com.example.traders.R
import com.example.traders.databinding.FragmentCryptoItemBinding
import com.example.traders.dialogs.confirmationDialog.ConfirmationDialogViewModel
import com.example.traders.watchlist.adapters.CryptoViewPagerAdapter
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class CryptoItemFragment : BaseFragment() {
    private val viewModel: CryptoItemViewModel by viewModels()
    private lateinit var binding: FragmentCryptoItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCryptoItemBinding.inflate(inflater, container, false)
        val receivedValues = CryptoItemFragmentArgs.fromBundle(requireArguments())

        binding.setUpUI(receivedValues)
        binding.setUpListeners()

        val crypto = FixedCryptoList.valueOf(receivedValues.symbol.uppercase())
        setUpTabs(binding.singleItemViewPager, binding.singleItemTablayout, crypto)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(viewModel) {
            lifecycleScope.launchWhenStarted {
                isFavouriteBtnActive.collect { isActive ->
                    binding.favouriteBtn.isEnabled = isActive
                }
            }
            lifecycleScope.launchWhenStarted {
                events.collect { event ->
                    when(event) {
                        CryptoItemEvents.AddToFavourites -> {
                            Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                        }
                        CryptoItemEvents.RemoveFromFavourites -> {
                            Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun FragmentCryptoItemBinding.setUpUI(receivedValues: CryptoItemFragmentArgs) {
        symbol.text = receivedValues.symbol
        if(receivedValues.isFavourite) {
           favouriteBtn.setImageResource(R.drawable.ic_star_active)
        }
    }

    private fun FragmentCryptoItemBinding.setUpListeners() {
        backButton.setOnClickListener {
            navController.navigateUp()
        }

        favouriteBtn.setOnClickListener {
//            change icon to the opposite
            if(!viewModel.isFavourite) {
                favouriteBtn.setImageResource(R.drawable.ic_star_active)
            } else {
                favouriteBtn.setImageResource(R.drawable.ic_star_inactive)
            }
            viewModel.symbol?.let { viewModel.onFavouriteBtnClicked(it) }
        }
    }

    private fun setUpTabs(viewPager: ViewPager2, tabLayout: TabLayout, crypto: FixedCryptoList) {
        val viewPagerAdapter =
            CryptoViewPagerAdapter(childFragmentManager, lifecycle, crypto)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setText(R.string.crypto_chart)
                }
                1 -> {
                    tab.setText(R.string.crypto_stats)
                }
                2 -> {
                    tab.setText(R.string.crypto_desc)
                }
            }
        }.attach()
    }
}
