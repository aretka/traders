package com.example.traders.dialogs.sellDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.FragmentSellDialogBinding
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.dialogs.buyDialog.BuyDialogViewModel
import com.example.traders.roundNumber
import kotlinx.coroutines.flow.collect

class SellDialogFragment(val lastPrice: Double, val symbol: String) : DialogFragment() {
    // this will be lateinit var and will receive sharedpref val
    private val cryptoBalance = 0.0245
    private lateinit var factory: SellViewModelFactory
    private lateinit var viewModel: SellDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            factory = SellViewModelFactory(symbol, lastPrice, cryptoBalance)
            viewModel = ViewModelProvider(this, factory)
                .get(SellDialogViewModel::class.java)

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = FragmentSellDialogBinding.inflate(inflater)
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

    private fun FragmentSellDialogBinding.initUI() {
        header.text = header.context.getString(R.string.sell_crypto, symbol)

        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoPrice.text = "$ " + roundNumber(lastPrice.toDouble())

        cryptoBalanceLabel.text = cryptoBalanceLabel.context.getString(R.string.crypto_balance_label, symbol)
        cryptoBalance.text = this@SellDialogFragment.cryptoBalance.toString()

        usdToGetLabel.text = usdToGetLabel.context.getString(R.string.usd_to_get_label)
        usdToGet.text = viewModel.state.value.usdToGet.toString()

        cryptoBalanceLeft.text = this@SellDialogFragment.cryptoBalance.toString()
        cryptoBalanceLeftLabel.text = cryptoBalanceLeftLabel.context.getString(R.string.crypto_balance_left_label, symbol)
    }

    private fun FragmentSellDialogBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.validateInput(enteredVal.toString())
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        sellBtn.setOnClickListener {
            dialog.dismiss()
        }

        maxBtn.setOnClickListener {
            priceInputField.setText(this@SellDialogFragment.cryptoBalance.toString())
        }
    }

    private fun FragmentSellDialogBinding.updateFields(state: SellState) {
        if(state.messageType == DialogValidationMessage.IS_TOO_LOW) {
            validationMessage.text = state.messageType.message
        } else {
            validationMessage.text = state.messageType.message
        }
        sellBtn.isEnabled = state.isBtnEnabled
        usdToGet.text = state.usdToGet.toString()
        cryptoBalanceLeft.text = state.cryptoLeft.toString()
    }
}


