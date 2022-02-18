package com.example.traders.dialogs.depositDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.databinding.DialogFragmentDepositBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DepositDialogFragment : DialogFragment() {
    private val viewModel: DepositViewModel by viewModels()

    private lateinit var binding: DialogFragmentDepositBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = DialogFragmentDepositBinding.inflate(inflater)
            val dialog = builder.setView(binding.root)
                .setCancelable(true)
                .create()
            binding.setUpListeners(dialog)
            collectState()
            collcetEvents()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun collcetEvents() {
        lifecycleScope.launchWhenCreated {
            viewModel.events.collect {
                when (it) {
                    is DepositDialogEvent.Dismiss -> dialog?.dismiss()
                }
            }
        }
    }

    private fun collectState() {
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect {
                binding.updateFields(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DialogFragmentDepositBinding.setUpListeners(dialog: AlertDialog) {
        usdInput.addTextChangedListener { enteredVal ->
            viewModel.onInputChanged(enteredVal.toString())
        }

        depositBtn.setOnClickListener {
            viewModel.onDepositButtonClicked()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun DialogFragmentDepositBinding.updateFields(it: DepositState) {
        validationMessage.text = it.validationMessage
        depositBtn.isEnabled = it.isBtnEnabled
    }
}
