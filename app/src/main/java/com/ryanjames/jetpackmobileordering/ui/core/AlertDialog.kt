package com.ryanjames.jetpackmobileordering.ui.core


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.core.StringResource
import com.ryanjames.jetpackmobileordering.ui.theme.*

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
                    TypeScaledTextView(label = stringResource(id =  alertDialogState.loadingText.id), typeScale = TypeScaleCategory.Subtitle1)
                }
            }

        }

        return
    }

    AlertDialog(
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = {},
        title = {
            TypeScaledTextView(label = alertDialogState.title, typeScale = TypeScaleCategory.H6)
        },
        text = {
            TypeScaledTextView(label = alertDialogState.message, typeScale = TypeScaleCategory.Subtitle1)
        },
        buttons = {
            Column {
                Row(modifier = Modifier
                    .padding(end = 16.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End) {

                    AccentButton(onClick = {
                        alertDialogState.dismissDialog()
                    }, label = "OK")
                }


                Spacer(modifier = Modifier.size(16.dp))
            }

        },
        backgroundColor = AppTheme.colors.materialColors.surface
    )
}

open class AlertDialogState(
    val title: String = "",
    val message: String = "",
    private val onDismiss: () -> Unit = {}
) {

    fun dismissDialog() {
        onDismiss.invoke()
    }

}

class LoadingDialogState(
    val loadingText: StringResource = StringResource(R.string.please_wait)
) : AlertDialogState()