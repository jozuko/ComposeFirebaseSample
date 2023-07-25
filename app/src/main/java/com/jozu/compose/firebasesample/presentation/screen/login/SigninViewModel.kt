package com.jozu.compose.firebasesample.presentation.screen.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jozu.compose.firebasesample.domain.Account
import com.jozu.compose.firebasesample.domain.AccountFuture
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarManager
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.jozu.compose.firebasesample.usecase.GoogleSigninCase
import com.jozu.compose.firebasesample.usecase.SignOutUsecase
import com.jozu.compose.firebasesample.usecase.SigninUsecase
import com.jozu.compose.firebasesample.usecase.SignupUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
@HiltViewModel
class SigninViewModel @Inject constructor(
    private val signinUsecase: SigninUsecase,
    private val signupUsecase: SignupUsecase,
    private val signOutUsecase: SignOutUsecase,
    private val googleSigninCase: GoogleSigninCase,
    accountRepository: AccountRepository,
) : ViewModel() {
    private val _uiState: MutableState<SigninUiState> = mutableStateOf(SigninUiState.initial)
    val uiState: State<SigninUiState> = _uiState

    /** ログイン中ユーザを返却するcallbackFlow */
    val accountState: StateFlow<AccountFuture<Account>> = accountRepository.accountFuture
        .map {
            // UiStateを更新
            _uiState.value = _uiState.value.updateCurrentUser(it)
            it
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AccountFuture.Idle,
        )

    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onPasswordConfirmChange(newValue: String) {
        _uiState.value = _uiState.value.copy(passwordConfirm = newValue)
    }

    fun onToSigninClick() {
        _uiState.value = _uiState.value.copy(isCreateUserMode = false)
    }

    fun onToSignupClick() {
        _uiState.value = _uiState.value.copy(isCreateUserMode = true)
    }

    fun onSigninClick() {
        val validatedMessage = _uiState.value.validateSignin()
        if (validatedMessage != 0) {
            SnackbarManager.showMessage(validatedMessage)
            return
        }

        _uiState.value.doSignProcess()
        viewModelScope.launch(
            context = CoroutineExceptionHandler { /*coroutineContext*/_, throwable ->
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                signinUsecase.signin(_uiState.value.email, _uiState.value.password)
            },
        )
    }

    fun onSignupClick() {
        val validatedMessage = _uiState.value.validateSignup()
        if (validatedMessage != 0) {
            SnackbarManager.showMessage(validatedMessage)
            return
        }

        _uiState.value.doSignProcess()
        viewModelScope.launch(
            context = CoroutineExceptionHandler { /*coroutineContext*/_, throwable ->
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                signupUsecase.signup(_uiState.value.email, _uiState.value.password)
            },
        )
    }

    fun onSignOutClick(activity: Activity) {
        viewModelScope.launch(
            context = CoroutineExceptionHandler { /*coroutineContext*/_, throwable ->
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                signOutUsecase.signOut(activity)
            },
        )
    }

    fun onClickSignInWithGoogleOneTap(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        viewModelScope.launch(
            context = CoroutineExceptionHandler { _, throwable ->
                val message = throwable.toSnackbarMessage()
                Log.e("onSignInWithGoogleClick", "Error!! $message")
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                googleSigninCase.signinOneTap(activity, launcher)
            },
        )
    }

    fun onResultSignInWithGoogleOneTap(activity: Activity, result: ActivityResult) {
        viewModelScope.launch(
            context = CoroutineExceptionHandler { _, throwable ->
                val message = throwable.toSnackbarMessage()
                Log.e("onSignInWithGoogleClick", "Error!! $message")
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                googleSigninCase.onResultSigninOneTap(activity, result)
            },
        )
    }
}