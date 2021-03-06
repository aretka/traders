package com.example.traders.presentation.dialogs.confirmationDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.traders.databinding.DialogConfirmationBinding
import com.example.traders.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmationDialogFragment(
    private val message: String,
    private val confirmationType: ConfirmationType
) : DialogFragment() {
    @Inject
    lateinit var viewModelAssistedFactory: ConfirmationDialogViewModel.Factory

    private val viewModel: ConfirmationDialogViewModel by viewModels() {
        ConfirmationDialogViewModel.provideFactory(viewModelAssistedFactory, confirmationType)
    }

    private lateinit var binding: DialogConfirmationBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = DialogConfirmationBinding.inflate(inflater)
            binding.confirmationMessage.text = message
            binding.setClickListeners()
            val dialog = builder.setView(binding.root)
                .setCancelable(false)
                .create()

            lifecycleScope.launchWhenCreated {
                viewModel.events.collect { event ->
                    when (event) {
                        ConfirmationDialogEvent.Dismiss -> {
                            dismissDialog()
                        }
                    }.exhaustive
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun DialogConfirmationBinding.setClickListeners() {
        successBtn.setOnClickListener {
            viewModel.onAcceptButtonClicked()
        }

        cancelBtn.setOnClickListener {
            viewModel.onCancelButtonClicked()
        }
    }

    private fun dismissDialog() {
        dialog?.dismiss()
    }

}
