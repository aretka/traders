package com.example.traders.dialogs.confirmationDialog

sealed class ConfirmationDialogEvent {
    object Dismiss: ConfirmationDialogEvent()
}