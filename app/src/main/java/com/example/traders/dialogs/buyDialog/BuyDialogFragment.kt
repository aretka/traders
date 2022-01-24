package com.example.traders.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.R
import com.example.traders.databinding.FragmentBuyDialogBinding
import com.example.traders.roundNumber
import kotlinx.coroutines.flow.collect

class BuyDialogFragment(val lastPrice: String, val symbol: String): DialogFragment() {

    // usdAccBalance will receive val from shared prefs which are stored in activity
    private val usdAccBalance = 1525.0
    private val viewModel: BuyDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater =  requireActivity().layoutInflater

            val view = FragmentBuyDialogBinding.inflate(inflater)
            val dialog = builder.setView(view.root)
                .setCancelable(true)
                .create()
            view.initUI()
            view.addListeners(dialog)
            lifecycleScope.launchWhenCreated {
                viewModel.state.collect {
                    view.updateValues(it)
                }
            }

            dialog
        }?: throw IllegalStateException("Activity cannot be null")
    }

//    private fun enteredValIsValid(enteredVal: String): Boolean {
//        return if(enteredVal.isBlank()) {
//            messageType = DialogValidationMessage.IS_EMPTY
//            false
//        } else if(enteredVal.toDouble() > usdAccBalance) {
//            messageType = DialogValidationMessage.IS_TOO_HIGH
//            false
//        } else if(enteredVal.toDouble() in MIN_INPUT_VAL..usdAccBalance) {
//            messageType = DialogValidationMessage.IS_VALID
//            true
//        } else if(enteredVal.toDouble() < MIN_INPUT_VAL) {
//            messageType = DialogValidationMessage.IS_TOO_LOW
//            false
//        } else {
//            messageType = DialogValidationMessage.IS_TOO_HIGH
//            false
//        }
//    }

    companion object {
        val MIN_INPUT_VAL = 10.0
        var messageType = DialogValidationMessage.IS_VALID
    }

    private fun FragmentBuyDialogBinding.initUI() {
        header.text = header.context.getString(R.string.buy_crypto, symbol)
        usdBalance.text = usdBalance.context.getString(R.string.usd_sign, roundNumber(usdAccBalance))

        cryptoPrice.text = cryptoPrice.context.getString(R.string.usd_sign, roundNumber(lastPrice.toDouble()))
        cryptoPriceLabel.text = cryptoPriceLabel.context.getString(R.string.price_of_coin, symbol)
        cryptoAmountLabel.text = cryptoAmountLabel.context.getString(R.string.coin_amount, symbol)
    }

    private fun FragmentBuyDialogBinding.addListeners(dialog: AlertDialog) {
        priceInputField.addTextChangedListener { enteredVal ->
            viewModel.validateInput(enteredVal.toString())
//            buyBtn.isEnabled = enteredValIsValid(enteredVal.toString())
//            validationMessage.text = messageType.message
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        buyBtn.setOnClickListener {
            dialog.dismiss()
        }

        maxBtn.setOnClickListener {
            priceInputField.setText(usdAccBalance.toString())
        }
    }

    private fun FragmentBuyDialogBinding.updateValues(state: BuyState) {
        validationMessage.text = state.messageType.message
        usdBalanceLeft.text = state.usdLeft.toString()
        cryptoToGet.text = state.cryptoToGet.toString()
        buyBtn.isEnabled = state.isBtnEnabled
    }
}




