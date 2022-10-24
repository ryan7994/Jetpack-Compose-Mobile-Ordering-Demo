package com.ryanjames.composemobileordering.ui.core


import androidx.compose.foundation.layout.*
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
import com.ryanjames.composemobileordering.ui.theme.*

@Composable
fun Dialog(alertDialogState: AlertDialogState?) {

    if (alertDialogState == null) return

    if (alertDialogState is LoadingDialogState) {
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
                        text = stringResource(id = alertDialogState.loadingText.id),
                        style = Typography.bodyLarge
                    )
                }
            }

        }

        return
    }


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
            Text(
                text = stringResource(alertDialogState.message.id),
                style = Typography.bodyLarge,
                color = AppTheme.colors.darkTextColor
            )
        },
        confirmButton  = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {

                    if (alertDialogState is TwoButtonsDialogState) {

                        AccentButton(onClick = {
                            alertDialogState.onClickNegativeBtn.invoke()
                        }, label = stringResource(alertDialogState.negativeButton.id))

                        Spacer(modifier = Modifier.size(8.dp))

                        AccentButton(onClick = {
                            alertDialogState.onClickPositiveBtn.invoke()
                        }, label = stringResource(alertDialogState.positiveButton.id))


                    } else {
                        AccentButton(onClick = {
                            alertDialogState.dismissDialog()
                        }, label = "OK")
                    }
                }


                Spacer(modifier = Modifier.size(16.dp))
            }

        }
    )
}

open class AlertDialogState(
    val title: StringResource?,
    val message: StringResource,
    private val onDismiss: () -> Unit = {}
) {

    fun dismissDialog() {
        onDismiss.invoke()
    }

}

class TwoButtonsDialogState(
    title: StringResource,
    message: StringResource,
    val positiveButton: StringResource,
    val negativeButton: StringResource,
    val onClickPositiveBtn: () -> Unit,
    val onClickNegativeBtn: () -> Unit
) : AlertDialogState(title, message)

class LoadingDialogState(
    val loadingText: StringResource = StringResource(R.string.please_wait)
) : AlertDialogState(title = null, message = loadingText)