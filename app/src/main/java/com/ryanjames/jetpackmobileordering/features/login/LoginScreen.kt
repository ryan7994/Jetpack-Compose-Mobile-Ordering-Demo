package com.ryanjames.jetpackmobileordering.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryanjames.jetpackmobileordering.R
import com.ryanjames.jetpackmobileordering.features.login.LoginFormField
import com.ryanjames.jetpackmobileordering.features.login.LoginViewModel
import com.ryanjames.jetpackmobileordering.features.login.LoginScreenState
import com.ryanjames.jetpackmobileordering.ui.core.Dialog
import com.ryanjames.jetpackmobileordering.ui.theme.*

@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    val state = loginViewModel.loginScreenState.collectAsState()
    LoginScreenSurface(
        loginScreenState = state.value,
        onValueChange = loginViewModel::onValueChange,
        onClickSignIn = loginViewModel::onClickSignIn
    )
}

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, onClick : () -> Unit) {
    val state = loginViewModel.loginScreenState.collectAsState()
    LoginScreenSurface(
        loginScreenState = state.value,
        onValueChange = loginViewModel::onValueChange,
        onClickSignIn = onClick
    )
}


@Composable
fun LoginScreenSurface(
    loginScreenState: LoginScreenState,
    onValueChange: (String, LoginFormField) -> Unit = { _, _ -> },
    onClickSignIn: () -> Unit = {}
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
                TypeScaledTextView(
                    label = "Let's get started.",
                    modifier = Modifier.align(CenterHorizontally),
                    typeScale = TypeScaleCategory.H3
                )
                Spacer(modifier = Modifier.size(32.dp))
                SingleLineTextField(
                    value = loginScreenState.username,
                    onValueChange = { onValueChange.invoke(it, LoginFormField.Username) },
                    hintText = "Username",
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
                    hintText = "Password",
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
                    label = "Sign In"
                )
                Spacer(modifier = Modifier.size(16.dp))
                TypeScaledTextView(
                    label = "Mobile Ordering Demo",
                    color = TextColor.LightTextColor,
                    typeScale = TypeScaleCategory.Subtitle1
                )
                TypeScaledTextView(
                    label = "Using Jetpack Compose",
                    color = TextColor.LightTextColor,
                    typeScale = TypeScaleCategory.Subtitle1
                )
            }


            Dialog(loginScreenState.alertDialogState)

        }

    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreenSurface(LoginScreenState())
}

@Preview
@Composable
fun PreviewLoginScreenDarkMode() {
    MyComposeAppTheme(darkTheme = true) {
        LoginScreenSurface(LoginScreenState())
    }
}