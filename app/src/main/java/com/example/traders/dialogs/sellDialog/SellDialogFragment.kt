package com.example.traders.dialogs.sellDialog

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
import com.example.traders.databinding.DialogFragmentSellBinding
import com.example.traders.dialogs.DialogValidationMessage
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

    private lateinit var binding: DialogFragmentSellBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = DialogFragmentSellBinding.inflate(inflater)
            val dialog = builder.setView(binding.root)
                .setCancelable(true)
                .create()
            binding.initUI()
            binding.addListeners(dialog)

            collectStateData()
            collectEventsData()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun collectEventsData() {
        lifecycleScope.launchWhenCreated {
            viewModel.events.collect { event ->
                when (event) {
                    is SellDialogEvent.Dismiss -> dialog?.dismiss()
                }
            }
        }
    }

    private fun collectStateData() {
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect {
                binding.updateFields(it)
            }
        }
    }

    private fun DialogFragmentSellBinding.initUI() {
        header.text = header.context.getString(R.string.sell_crypto, symbol)

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoPrice.text = "$ " + lastPrice.toString()

        cryptoBalanceLabel.text =
            cryptoBalanceLabel.context.getString(R.string.crypto_balance_label, symbol)

        usdToGetLabel.text = usdToGetLabel.context.getString(R.string.usd_to_get_label)
        usdToGet.text = viewModel.state.value.usdToGet.toString()

        cryptoBalanceLeft.text = viewModel.state.value.cryptoLeft.toString()
        cryptoBalanceLeftLabel.text =
            cryptoBalanceLeftLabel.context.getString(R.string.crypto_balance_left_label, symbol)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DialogFragmentSellBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.onInputChanged(enteredVal.toString())
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        sellBtn.setOnClickListener {
            viewModel.onSellButtonClicked()
        }

        maxBtn.setOnClickListener {
            priceInputField.setText(viewModel.state.value.cryptoBalance?.amount.toString() ?: "0")
        }
    }

    private fun DialogFragmentSellBinding.updateFields(state: SellState) {
        if (state.messageType == DialogValidationMessage.IS_TOO_LOW) {
            validationMessage.text =
                state.messageType.message + viewModel.state.value.minInputVal.toString()
        } else {
            validationMessage.text = state.messageType.message
        }
        cryptoBalance.text = viewModel.state.value.cryptoBalance?.amount.toString() ?: ""
        sellBtn.isEnabled = state.isBtnEnabled
        usdToGet.text = state.usdToGet.toString()
        cryptoBalanceLeft.text = state.cryptoLeft.toString()
    }
}


