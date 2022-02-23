package com.example.traders.dialogs.buyDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.DialogFragmentBuyBinding
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class BuyDialogFragment(val lastPrice: BigDecimal, val crypto: FixedCryptoList) : DialogFragment() {

    @Inject
    lateinit var viewModelAssistedFactory: BuyDialogViewModel.Factory

    private val viewModel: BuyDialogViewModel by viewModels() {
        BuyDialogViewModel.provideFactory(viewModelAssistedFactory, crypto, lastPrice)
    }

    private lateinit var binding: DialogFragmentBuyBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = DialogFragmentBuyBinding.inflate(inflater)
            val dialog = builder.setView(binding.root)
                .setCancelable(true)
                .create()
            binding.initUI()
            binding.addListeners(dialog)
            collectViewModelState()
            collectEvents()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun collectViewModelState() {
        lifecycleScope.launchWhenCreated {
            with(viewModel) {
                state.collect { state ->
                    binding.updateValues(state)
                }
            }
        }
    }

    private fun collectEvents() {
        lifecycleScope.launchWhenCreated {
            with(viewModel) {
                events.collect { event ->
                    when (event) {
                        is BuyDialogEvent.Dismiss -> dismissDialog()
                    }
                }
            }
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

    private fun DialogFragmentBuyBinding.initUI() {
        header.text = header.context.getString(R.string.buy_crypto, crypto.name)

        cryptoPrice.text =
            cryptoPrice.context.getString(
                R.string.usd_sign,
                lastPrice.toString()
            )

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, crypto.name)
        cryptoAmountLabel.text = cryptoAmountLabel.context.getString(R.string.coin_amount, crypto.name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DialogFragmentBuyBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.onInputChanged(enteredVal.toString())
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        buyBtn.setOnClickListener {
            viewModel.onBuyButtonClicked()
        }

        maxBtn.setOnClickListener {
            priceInputField.setText(viewModel.state.value.usdBalance.amount.toString())
        }
    }

    private fun DialogFragmentBuyBinding.updateValues(state: BuyState) {
        if (state.messageType == DialogValidationMessage.IS_TOO_LOW) {
            validationMessage.text =
                state.messageType.message + viewModel.state.value.minInputVal.toString()
        } else {
            validationMessage.text = state.messageType.message
        }
        usdBalance.text =
            usdBalance.context.getString(
                R.string.usd_sign,
                viewModel.state.value.usdBalance.amount.toString()
            )
        usdBalanceLeft.text = state.usdLeft.toString()
        cryptoToGet.text = state.cryptoToGet.toString()
        buyBtn.isEnabled = state.isBtnEnabled
    }
}




