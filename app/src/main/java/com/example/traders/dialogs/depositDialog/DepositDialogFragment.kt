package com.example.traders.dialogs.depositDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.databinding.DialogFragmentDepositBinding
import com.example.traders.databinding.DialogFragmentSellBinding
import com.example.traders.dialogs.DialogValidationMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DepositDialogFragment: DialogFragment() {
    private val viewModel: DialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val view = DialogFragmentDepositBinding.inflate(inflater)
            val dialog = builder.setView(view.root)
                .setCancelable(true)
                .create()
            view.setUpListeners()
            lifecycleScope.launchWhenCreated {
                viewModel.state.collect {
                    view.updateFields(it)
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun DialogFragmentDepositBinding.setUpListeners() {
        usdInput.addTextChangedListener { enteredVal ->
            viewModel.validateInput(enteredVal.toString())
        }
    }

    private fun DialogFragmentDepositBinding.updateFields(it: DepositState) {
        validationMessage.text = it.validationMessage
        depositBtn.isEnabled = it.isBtnEnabled
    }
}
