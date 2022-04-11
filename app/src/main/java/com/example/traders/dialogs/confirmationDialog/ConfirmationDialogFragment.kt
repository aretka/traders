package com.example.traders.dialogs.confirmationDialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.traders.databinding.DialogConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationDialogFragment : DialogFragment() {
    private lateinit var binding: DialogConfirmationBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            binding = DialogConfirmationBinding.inflate(inflater)
            val dialog = builder.setView(binding.root)
                .setCancelable(false)
                .create()

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}