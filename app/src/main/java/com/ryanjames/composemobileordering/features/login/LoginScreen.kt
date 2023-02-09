package com.ryanjames.composemobileordering.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.core.AccentTextButton
import com.ryanjames.composemobileordering.ui.core.Dialog
import com.ryanjames.composemobileordering.ui.core.FullWidthButton
import com.ryanjames.composemobileordering.ui.core.SingleLineTextField
import com.ryanjames.composemobileordering.ui.theme.*

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, onClickSignUp: () -> Unit) {
    val state = loginViewModel.loginScreenState.collectAsState()
    LoginScreenLayout(
        loginScreenState = state.value,
        onValueChange = loginViewModel::onValueChange,
        onClickSignIn = loginViewModel::onClickSignIn,
        onClickSignUp = onClickSignUp
    )
}


@Composable
fun LoginScreenLayout(
    loginScreenState: LoginScreenState,
    onValueChange: (String, LoginFormField) -> Unit = { _, _ -> },
    onClickSignIn: () -> Unit = {},
    onClickSignUp: () -> Unit = {}
) {
    Surface(
        color = AppTheme.colors.materialColors.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(48.dp))
                .background(AppTheme.colors.materialColors.background)
        ) {
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier
                    .padding(start = 32.dp, end = 32.dp)
                    .fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.order_food),
                    contentDescription = "",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(R.string.lets_get_started),
                    modifier = Modifier.align(CenterHorizontally),
                    style = Typography.headlineLarge,
                    color = AppTheme.colors.materialColors.onBackground
                )
                Spacer(modifier = Modifier.size(32.dp))
                SingleLineTextField(
                    modifier = Modifier.testTag("Username"),
                    value = loginScreenState.username,
                    onValueChange = { onValueChange.invoke(it, LoginFormField.Username) },
                    hintText = stringResource(R.string.username),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        autoCorrect = false
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                )
                Spacer(modifier = Modifier.size(16.dp))
                SingleLineTextField(
                    value = loginScreenState.password,
                    onValueChange = { onValueChange.invoke(it, LoginFormField.Password) },
                    hintText = stringResource(R.string.password),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        autoCorrect = false
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.size(32.dp))
                FullWidthButton(
                    onClick = { onClickSignIn.invoke() },
                    label = stringResource(R.string.sign_in),
                    tag = "btnSignIn"
                )
                AccentTextButton(
                    onClick = { onClickSignUp.invoke() },
                    label = "Sign Up"
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(R.string.mobile_ordering_demo),
                    color = AppTheme.colors.lightTextColor,
                    style = Typography.bodyLarge
                )
                Text(
                    text = stringResource(R.string.using_jetpack_compose),
                    color = AppTheme.colors.lightTextColor,
                    style = Typography.bodyLarge
                )
            }


            Dialog(loginScreenState.alertDialogState)

        }

    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreenLayout(LoginScreenState())
}

@Preview
@Composable
fun PreviewLoginScreenDarkMode() {
    MyComposeAppTheme(darkTheme = true) {
        LoginScreenLayout(LoginScreenState())
    }
}