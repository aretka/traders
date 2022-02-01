package com.example.traders.dialogs.sellDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.DialogFragmentSellBinding
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.roundAndFormatDouble
import com.example.traders.roundNum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class SellDialogFragment(val lastPrice: BigDecimal, val symbol: String) : DialogFragment() {

    @Inject
    lateinit var assistedViewModelFactory: SellDialogViewModel.Factory

    private val viewModel: SellDialogViewModel by viewModels {
        SellDialogViewModel.provideFactory(assistedViewModelFactory, symbol, lastPrice)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = DialogFragmentSellBinding.inflate(inflater)
            val dialog = builder.setView(view.root)
                .setCancelable(true)
                .create()
            view.initUI()
            view.addListeners(dialog)

            lifecycleScope.launchWhenCreated {
                viewModel.state.collect {
                    view.updateFields(it)
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun DialogFragmentSellBinding.initUI() {
        header.text = header.context.getString(R.string.sell_crypto, symbol)

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoPrice.text = "$ " + lastPrice.toString()

        cryptoBalanceLabel.text =
            cryptoBalanceLabel.context.getString(R.string.crypto_balance_label, symbol)
        cryptoBalance.text = viewModel.state.value.cryptoBalance.toString()

        usdToGetLabel.text = usdToGetLabel.context.getString(R.string.usd_to_get_label)
        usdToGet.text = viewModel.state.value.usdToGet.toString()

        cryptoBalanceLeft.text = viewModel.state.value.cryptoLeft.toString()
        cryptoBalanceLeftLabel.text =
            cryptoBalanceLeftLabel.context.getString(R.string.crypto_balance_left_label, symbol)
    }

    private fun DialogFragmentSellBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.validateInput(enteredVal.toString())
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        sellBtn.setOnClickListener {
            viewModel.updateBalance()
            dialog.dismiss()
        }

        maxBtn.setOnClickListener {
            priceInputField.setText(viewModel.state.value.cryptoBalance.toString())
        }
    }

    private fun DialogFragmentSellBinding.updateFields(state: SellState) {
        if (state.messageType == DialogValidationMessage.IS_TOO_LOW) {
            validationMessage.text = state.messageType.message + viewModel.state.value.minInputVal.toString()
        } else {
            validationMessage.text = state.messageType.message
        }
        sellBtn.isEnabled = state.isBtnEnabled
        usdToGet.text = state.usdToGet.toString()
        cryptoBalanceLeft.text = state.cryptoLeft.toString()
    }
}


