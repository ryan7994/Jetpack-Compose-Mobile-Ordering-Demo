package com.ryanjames.composemobileordering.ui.core


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.ui.theme.AppTheme
import com.ryanjames.composemobileordering.ui.theme.CoralRed
import com.ryanjames.composemobileordering.ui.theme.Typography

@Composable
fun Dialog(alertDialogState: AlertDialogState?) {

    when (alertDialogState) {
        is LoadingDialogState -> LoadingDialog(alertDialogState)
        is DismissibleDialogState -> DismissibleDialog(alertDialogState)
        is TwoButtonsDialogState -> TwoButtonsDialog(alertDialogState)
        null -> return

    }
}

@Composable
private fun TwoButtonsDialog(alertDialogState: TwoButtonsDialogState) {
    AlertDialog(
        containerColor = AppTheme.colors.materialColors.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = {},
        title = {
            if (alertDialogState.title != null) {
                Text(
                    text = stringResource(id = alertDialogState.title.id),
                    style = Typography.titleLarge,
                    color = AppTheme.colors.darkTextColor
                )
            }
        },
        text = {
            if (alertDialogState.message != null) {
                Text(
                    text = stringResource(alertDialogState.message.id),
                    style = Typography.bodyLarge,
                    color = AppTheme.colors.darkTextColor
                )
            } else if (alertDialogState.stringMessage != null) {
                Text(
                    text = alertDialogState.stringMessage,
                    style = Typography.bodyLarge,
                    color = AppTheme.colors.darkTextColor
                )
            }
        },
        confirmButton = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    AccentButton(onClick = {
                        alertDialogState.onClickNegativeBtn.invoke()
                    }, label = stringResource(alertDialogState.negativeButton.id))

                    Spacer(modifier = Modifier.size(8.dp))

                    AccentButton(onClick = {
                        alertDialogState.onClickPositiveBtn.invoke()
                    }, label = stringResource(alertDialogState.positiveButton.id))
                }
                Spacer(modifier = Modifier.size(16.dp))
            }

        }
    )
}

@Composable
private fun DismissibleDialog(alertDialogState: DismissibleDialogState) {
    AlertDialog(
        containerColor = AppTheme.colors.materialColors.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = {},
        title = {
            if (alertDialogState.title != null) {
                Text(
                    text = stringResource(id = alertDialogState.title.id),
                    style = Typography.titleLarge,
                    color = AppTheme.colors.darkTextColor
                )
            }
        },
        text = {
            if (alertDialogState.message != null) {
                Text(
                    text = stringResource(alertDialogState.message.id),
                    style = Typography.bodyLarge,
                    color = AppTheme.colors.darkTextColor
                )
            } else if (alertDialogState.stringMessage != null) {
                Text(
                    text = alertDialogState.stringMessage,
                    style = Typography.bodyLarge,
                    color = AppTheme.colors.darkTextColor
                )
            }
        },
        confirmButton = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    AccentButton(
                        onClick = {
                            alertDialogState.onDismissDialog?.let { it() }
                        },
                        label = stringResource(id = alertDialogState.dismissCta.id)
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
            }

        }
    )
}

@Composable
private fun LoadingDialog(loadingDialogState: LoadingDialogState) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(shape = RoundedCornerShape(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(color = CoralRed)
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = loadingDialogState.loadingText.id),
                    style = Typography.bodyLarge
                )
            }
        }

    }
}

sealed class AlertDialogState(
    val title: StringResource? = null,
    val message: StringResource? = null,
    val stringMessage: String? = null
)

data class DismissibleDialogState(
    val dialogTitle: StringResource? = null,
    val dialogMessage: StringResource? = null,
    val dialogStringMessage: String? = null,
    val dismissCta: StringResource = StringResource(R.string.ok),
    val onDismissDialog: (() -> Unit)? = null
) : AlertDialogState(dialogTitle, dialogMessage, dialogStringMessage)

data class TwoButtonsDialogState(
    val dialogTitle: StringResource,
    val dialogMessage: StringResource,
    val positiveButton: StringResource,
    val negativeButton: StringResource,
    val onClickPositiveBtn: () -> Unit,
    val onClickNegativeBtn: () -> Unit
) : AlertDialogState(dialogTitle, dialogTitle)

data class LoadingDialogState(
    val loadingText: StringResource = StringResource(R.string.please_wait)
) : AlertDialogState(title = null, message = loadingText)