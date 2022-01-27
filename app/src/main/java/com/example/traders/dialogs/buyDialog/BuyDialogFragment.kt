package com.example.traders.dialogs.buyDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.FragmentBuyDialogBinding
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.roundAndFormatNum
import com.example.traders.roundNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class BuyDialogFragment(val lastPrice: Double, val symbol: String) : DialogFragment() {

    @Inject
    lateinit var viewModelAssistedFactory: BuyDialogViewModel.Factory

    private val viewModel: BuyDialogViewModel by viewModels() {
        BuyDialogViewModel.provideFactory(viewModelAssistedFactory, symbol, lastPrice)
    }
//    lateinit var binding: FragmentBuyDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val binding = FragmentBuyDialogBinding.inflate(inflater)
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

    private fun FragmentBuyDialogBinding.initUI() {
        header.text = header.context.getString(R.string.buy_crypto, symbol)
        usdBalance.text =
            usdBalance.context.getString(
                R.string.usd_sign,
                roundAndFormatNum(viewModel.state.value.usdBalance)
            )

        cryptoPrice.text =
            cryptoPrice.context.getString(
                R.string.usd_sign,
                roundAndFormatNum(lastPrice, viewModel.state.value.priceNumToRound)
            )

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoAmountLabel.text = cryptoAmountLabel.context.getString(R.string.coin_amount, symbol)
    }

    private fun FragmentBuyDialogBinding.addListeners(dialog: AlertDialog) {
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
            priceInputField.setText(viewModel.state.value.usdBalance.toString())
        }
    }

    private fun FragmentBuyDialogBinding.updateValues(state: BuyState) {
        if(state.messageType == DialogValidationMessage.IS_TOO_LOW) {
            validationMessage.text = state.messageType.message + viewModel.state.value.minInputVal.toString()
        } else {
            validationMessage.text = state.messageType.message
        }
        usdBalanceLeft.text = roundAndFormatNum(state.usdLeft)
        cryptoToGet.text = roundAndFormatNum(state.cryptoToGet, state.amountToRound)
        buyBtn.isEnabled = state.isBtnEnabled
    }
}




