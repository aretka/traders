package com.example.traders.presentation.dialogs.confirmationDialog

sealed class ConfirmationDialogEvent {
    object Dismiss: ConfirmationDialogEvent()
}