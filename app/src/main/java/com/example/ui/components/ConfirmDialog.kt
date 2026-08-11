package com.example.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.localization.AppStrings
import com.example.model.AppLanguage

@Composable
fun ConfirmDeleteDialog(
    titleKey: String = "confirm_delete_title",
    messageKey: String = "confirm_delete_msg",
    currentLanguage: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val title = AppStrings.get(titleKey, currentLanguage)
    val message = AppStrings.get(messageKey, currentLanguage)
    val deleteText = AppStrings.get("delete", currentLanguage)
    val cancelText = AppStrings.get("cancel", currentLanguage)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = deleteText,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelText)
            }
        }
    )
}
