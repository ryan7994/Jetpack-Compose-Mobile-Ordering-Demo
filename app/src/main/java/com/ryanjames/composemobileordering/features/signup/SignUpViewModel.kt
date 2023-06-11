package com.ryanjames.composemobileordering.features.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryanjames.composemobileordering.R
import com.ryanjames.composemobileordering.collectResource
import com.ryanjames.composemobileordering.constants.ERROR_CODE_LOGIN_FAILURE
import com.ryanjames.composemobileordering.core.Resource
import com.ryanjames.composemobileordering.core.StringResource
import com.ryanjames.composemobileordering.features.login.LoginEvent
import com.ryanjames.composemobileordering.network.model.Event
import com.ryanjames.composemobileordering.network.model.request.EnrollRequest
import com.ryanjames.composemobileordering.repository.AccountRepository
import com.ryanjames.composemobileordering.ui.core.AlertDialogState
import com.ryanjames.composemobileordering.ui.core.DialogManager
import com.ryanjames.composemobileordering.ui.core.LoadingDialogState
import com.ryanjames.composemobileordering.util.TextFieldValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val dialogManager: DialogManager
) : ViewModel(), DialogManager by dialogManager {

    private val _signUpViewState = MutableStateFlow(SignUpScreenState())
    val signUpViewState = _signUpViewState.asStateFlow()

    private val _autoLoginEvent: MutableStateFlow<Event<LoginEvent>> = MutableStateFlow(Event(LoginEvent.NoEvent))
    val autoLoginEvent: StateFlow<Event<LoginEvent>>
        get() = _autoLoginEvent

    private val requiredFields = listOf(
        SignUpFormField.Username,
        SignUpFormField.Password,
        SignUpFormField.Email,
        SignUpFormField.PhoneNumber,
        SignUpFormField.ConfirmPassword,
        SignUpFormField.FirstName,
        SignUpFormField.LastName
    )

    private val listOfFieldsNotFocusedYet = requiredFields.map { it::class }.toMutableList()

    fun onValueChange(text: String, signUpFormField: SignUpFormField) {
        when (signUpFormField) {
            SignUpFormField.ConfirmPassword -> _signUpViewState.update { signUpViewState.value.copy(confirmPassword = text) }
            SignUpFormField.Email -> _signUpViewState.update { signUpViewState.value.copy(email = text) }
            SignUpFormField.FirstName -> _signUpViewState.update { signUpViewState.value.copy(firstName = text) }
            SignUpFormField.LastName -> _signUpViewState.update { signUpViewState.value.copy(lastName = text) }
            SignUpFormField.Password -> _signUpViewState.update { signUpViewState.value.copy(password = text) }
            SignUpFormField.PhoneNumber -> _signUpViewState.update { signUpViewState.value.copy(phoneNumber = text) }
            SignUpFormField.Username -> _signUpViewState.update { signUpViewState.value.copy(username = text) }
        }
    }

    private fun removeAndGetListOfFieldsWithError(field: SignUpFormField): List<SignUpFormField> {
        return signUpViewState.value.fieldsWithError.filter { it != field }
    }

    private fun addAndGetListOfFieldsWithError(field: SignUpFormField): List<SignUpFormField> {
        return signUpViewState.value.fieldsWithError.toMutableList().apply {
            add(field)
        }
    }

    fun onFocusChanged(isFocused: Boolean, signUpFormField: SignUpFormField) {
        if (isFocused) {
            _signUpViewState.update { signUpViewState.value.copy(fieldsWithError = removeAndGetListOfFieldsWithError(signUpFormField)) }
            listOfFieldsNotFocusedYet.remove(signUpFormField::class)
        } else {
            // Don't validate field if the field hasn't been focused yet by the user.
            if (listOfFieldsNotFocusedYet.contains(signUpFormField::class)) return

            // Don't validate if field is not required
            if (!isFieldRequired(signUpFormField)) return

            if (isValid(signUpFormField)) {
                _signUpViewState.update { signUpViewState.value.copy(fieldsWithError = removeAndGetListOfFieldsWithError(signUpFormField)) }
            } else {
                _signUpViewState.update { signUpViewState.value.copy(fieldsWithError = addAndGetListOfFieldsWithError(signUpFormField)) }
            }
        }
    }

    private fun isFieldRequired(signUpFormField: SignUpFormField): Boolean = requiredFields.contains(signUpFormField)

    private fun isValid(signUpFormField: SignUpFormField): Boolean {
        return when (signUpFormField) {
            SignUpFormField.ConfirmPassword -> isConfirmPasswordValid()
            SignUpFormField.Email -> TextFieldValidator.isEmailValid(signUpViewState.value.email)
            SignUpFormField.FirstName -> isFirstNameValid()
            SignUpFormField.LastName -> isLastNameValid()
            SignUpFormField.Password -> TextFieldValidator.isPasswordValid(signUpViewState.value.password)
            SignUpFormField.PhoneNumber -> TextFieldValidator.isPhoneValid(signUpViewState.value.phoneNumber)
            SignUpFormField.Username -> TextFieldValidator.isUsernameValid(signUpViewState.value.username)
        }
    }

    private fun isConfirmPasswordValid(): Boolean =
        TextFieldValidator.isPasswordValid(signUpViewState.value.confirmPassword) && signUpViewState.value.password == signUpViewState.value.confirmPassword

    private fun isFirstNameValid(): Boolean = signUpViewState.value.firstName.isNotBlank()

    private fun isLastNameValid(): Boolean = signUpViewState.value.lastName.isNotBlank()

    private fun isFormValid(): Boolean {
        val listOfFieldsWithError = requiredFields.filter { !isValid(it) }
        _signUpViewState.update { signUpViewState.value.copy(fieldsWithError = listOfFieldsWithError) }
        return listOfFieldsWithError.isEmpty()
    }

    fun onClickJoin() {
        if (isFormValid()) {
            makeEnrollCall()
        }
    }

    private fun makeEnrollCall() {
        with(signUpViewState.value) {
            val enrollRequest = EnrollRequest(
                username = username,
                password = password,
                phoneNumber = phoneNumber,
                phoneNumberCountryCode = "US",
                firstName = firstName,
                lastName = lastName,
                email = email
            )

            viewModelScope.launch {
                accountRepository.enrollAndLogin(enrollRequest).collectResource(
                    onLoading = {
                        dialogManager.showDialog(LoadingDialogState(StringResource(R.string.enrolling)))
                    },
                    onSuccess = {
                        dialogManager.hideDialog()
                        _autoLoginEvent.update { Event(LoginEvent.LoginSuccess) }
                    },
                    onError = this@SignUpViewModel::onEnrollFailure
                )
            }
        }
    }

    private fun onEnrollFailure(errorResource: Resource.Error) {
        when(errorResource) {
            is Resource.Error.Custom -> {
                val apiErrorMessage = errorResource.error.apiErrorMessage

                if (errorResource.error.userDefinedErrorCode == ERROR_CODE_LOGIN_FAILURE) {
                    dialogManager.showDialog(
                        AlertDialogState(
                            title = StringResource(R.string.login_failed),
                            message = StringResource(R.string.enroll_login_failed),
                            onDismiss = dialogManager::hideDialog
                        )
                    )
                } else if (apiErrorMessage != null) {
                    dialogManager.showDialog(
                        AlertDialogState(
                            title = StringResource(R.string.enrollment_failed),
                            stringMessage = apiErrorMessage,
                            onDismiss = dialogManager::hideDialog
                        )
                    )
                }
            }
            is Resource.Error.Generic ->   dialogManager.showDialog(
                AlertDialogState(
                    title = StringResource(R.string.enrollment_failed),
                    message = StringResource(R.string.generic_error_message),
                    onDismiss = dialogManager::hideDialog
                )
            )
        }
    }
}