package com.example.traders.dialogs.buyDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.DialogFragmentBuyBinding
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.roundAndFormatDouble
import com.example.traders.roundNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class BuyDialogFragment(val lastPrice: BigDecimal, val symbol: String) : DialogFragment() {

    @Inject
    lateinit var viewModelAssistedFactory: BuyDialogViewModel.Factory

    private val viewModel: BuyDialogViewModel by viewModels() {
        BuyDialogViewModel.provideFactory(viewModelAssistedFactory, symbol, lastPrice)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val binding = DialogFragmentBuyBinding.inflate(inflater)
            val dialog = builder.setView(binding.root)
                .setCancelable(true)
                .create()
            binding.initUI()
            binding.addListeners(dialog)

            lifecycleScope.launchWhenCreated {
                viewModel.state.collect {
                    binding.updateValues(it)
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun DialogFragmentBuyBinding.initUI() {
        header.text = header.context.getString(R.string.buy_crypto, symbol)

        cryptoPrice.text =
            cryptoPrice.context.getString(
                R.string.usd_sign,
                lastPrice.toString()
            )

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoAmountLabel.text = cryptoAmountLabel.context.getString(R.string.coin_amount, symbol)
    }

    private fun DialogFragmentBuyBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.validateInput(enteredVal.toString())
        }

        cancelBtn.setOnClickListener {
            // TODO remove this function later
            viewModel.add1000UsdToBalance()
            dialog.dismiss()
        }

        buyBtn.setOnClickListener {
            viewModel.updateBalance()
            dialog.dismiss()
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




