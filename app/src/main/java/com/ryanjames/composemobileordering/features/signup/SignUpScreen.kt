package com.ryanjames.composemobileordering.features.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.ui.core.FullWidthButton
import com.ryanjames.composemobileordering.ui.core.SingleLineTextField
import com.ryanjames.composemobileordering.ui.core.TopAppBarWithUpButton
import com.ryanjames.composemobileordering.ui.theme.AppTheme
import com.ryanjames.composemobileordering.ui.theme.Typography

@Composable
fun SignUpScreen(signUpViewModel: SignUpViewModel, onUpBtnClicked: () -> Unit) {
    val state = signUpViewModel.signUpViewState.collectAsState()
    SignUpScreenLayout(
        signUpScreenState = state.value,
        onValueChange = signUpViewModel::onValueChange,
        onClickJoin = signUpViewModel::onClickJoin,
        onFocusChanged = signUpViewModel::onFocusChanged,
        onUpBtnClicked = onUpBtnClicked
    )
}

@Composable
fun SignUpScreenLayout(
    signUpScreenState: SignUpScreenState,
    onValueChange: (newValue: String, field: SignUpFormField) -> Unit,
    onClickJoin: () -> Unit,
    onFocusChanged: (isFocused: Boolean, field: SignUpFormField) -> Unit,
    onUpBtnClicked: () -> Unit
) {

    Scaffold(topBar = {
        TopAppBarWithUpButton(stringResource(R.string.sign_up), onUpBtnClicked)
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.materialColors.background)
                .padding(it)
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(R.string.all_fields_required),
                    style = Typography.bodyMedium, color =
                    AppTheme.colors.darkTextColor
                )

                Spacer(modifier = Modifier.size(16.dp))

                SingleLineTextField(
                    value = signUpScreenState.username,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.Username) },
                    hintText = stringResource(R.string.username),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.Username),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.Username) },
                    errorMessage = stringResource(R.string.username_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.password,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.Password) },

                    hintText = stringResource(R.string.password),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.Password),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.Password) },
                    errorMessage = stringResource(R.string.password_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.confirmPassword,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.ConfirmPassword) },
                    hintText = stringResource(R.string.confirm_password),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.ConfirmPassword),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.ConfirmPassword) },
                    errorMessage = stringResource(R.string.confirm_password_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.firstName,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.FirstName) },
                    hintText = stringResource(R.string.first_name),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.FirstName),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.FirstName) },
                    errorMessage = stringResource(R.string.first_name_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.lastName,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.LastName) },
                    hintText = stringResource(R.string.last_name),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.LastName),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.LastName) },
                    errorMessage = stringResource(R.string.last_name_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.email,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.Email) },
                    hintText = stringResource(R.string.email),
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.Email),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.Email) },
                    errorMessage = stringResource(R.string.email_error)
                )
                Spacer(modifier = Modifier.size(8.dp))

                SingleLineTextField(
                    value = signUpScreenState.phoneNumber,
                    onValueChange = { newValue -> onValueChange.invoke(newValue, SignUpFormField.PhoneNumber) },
                    hintText = stringResource(R.string.phone_number),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = signUpScreenState.fieldsWithError.contains(SignUpFormField.PhoneNumber),
                    onFocusChanged = { focusState -> onFocusChanged.invoke(focusState.isFocused, SignUpFormField.PhoneNumber) },
                    errorMessage = stringResource(R.string.phone_number_error)
                )
                Spacer(modifier = Modifier.size(16.dp))

                FullWidthButton(onClick = onClickJoin, label = stringResource(id = R.string.join))
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

    }


}