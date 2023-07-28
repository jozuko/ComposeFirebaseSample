package com.jozu.compose.firebasesample.presentation.screen.login

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jozu.compose.firebasesample.domain.Account
import com.jozu.compose.firebasesample.domain.AccountFuture
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarManager
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import com.jozu.compose.firebasesample.presentation.screen.ViewModelBase
import com.jozu.compose.firebasesample.usecase.GoogleLegacySigninCase
import com.jozu.compose.firebasesample.usecase.GoogleOneTapSigninCase
import com.jozu.compose.firebasesample.usecase.MailLinkCase
import com.jozu.compose.firebasesample.usecase.SignOutUsecase
import com.jozu.compose.firebasesample.usecase.SigninUsecase
import com.jozu.compose.firebasesample.usecase.SignupUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val googleOneTapSigninCase: GoogleOneTapSigninCase,
    private val googleLegacySigninCase: GoogleLegacySigninCase,
    private val mailLinkCase: MailLinkCase,
    accountRepository: AccountRepository,
) : ViewModelBase() {
    private val _uiState: MutableState<SigninUiState> = mutableStateOf(SigninUiState.initial)
    val uiState: State<SigninUiState> = _uiState

    /** ログイン中ユーザを返却するcallbackFlow */
    val accountState: StateFlow<AccountFuture<Account>> = accountRepository.accountFuture.map {
        // UiStateを更新
        _uiState.value = _uiState.value.updateCurrentUser(it)
        it
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = AccountFuture.Idle,
    )

    fun onChangeEmail(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onChangePassword(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onChangePasswordConfirm(newValue: String) {
        _uiState.value = _uiState.value.copy(passwordConfirm = newValue)
    }

    fun onClickToSignin() {
        _uiState.value = _uiState.value.copy(isCreateUserMode = false)
    }

    fun onClickToSignup() {
        _uiState.value = _uiState.value.copy(isCreateUserMode = true)
    }

    fun onChangeMailLinkEmail(newValue: String) {
        _uiState.value = _uiState.value.copy(mailLinkEmail = newValue)
    }

    fun onSigninClick() {
        val validatedMessage = _uiState.value.validateSignin()
        if (validatedMessage != 0) {
            SnackbarManager.showMessage(validatedMessage)
            return
        }

        _uiState.value.doSignProcess()
        launchCatching("onSigninClick") {
            signinUsecase.signin(_uiState.value.email, _uiState.value.password)
        }
    }

    fun onSignupClick() {
        val validatedMessage = _uiState.value.validateSignup()
        if (validatedMessage != 0) {
            SnackbarManager.showMessage(validatedMessage)
            return
        }

        _uiState.value.doSignProcess()
        launchCatching("onSignupClick") {
            signupUsecase.signup(_uiState.value.email, _uiState.value.password)
        }
    }

    fun onSignOutClick() {
        launchCatching("onSignOutClick") {
            signOutUsecase.signOut()
        }
    }

    fun onClickMailLinkSignin() {
        val validatedMessage = _uiState.value.validateMailLink()
        if (validatedMessage != 0) {
            SnackbarManager.showMessage(validatedMessage)
            return
        }

        launchCatching("onClickMailLinkSignin") {
            mailLinkCase.sendMailLink(_uiState.value.mailLinkEmail)
        }
    }

    fun onReceiveMailLink(mailLink: String?) {
        launchCatching("onReceiveMailLink") {
            mailLinkCase.signinMailLink(mailLink)
        }
    }

    fun onClickSignInWithGoogleOneTap(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        launchCatching("onClickSignInWithGoogleOneTap") {
            googleOneTapSigninCase.signin(launcher)
        }
    }

    fun onResultSignInWithGoogleOneTap(result: ActivityResult) {
        launchCatching("onResultSignInWithGoogleOneTap") {
            googleOneTapSigninCase.onResultSignin(result)
        }
    }

    fun onClickSignInWithGoogleLegacy(launcher: ActivityResultLauncher<Intent>) {
        try {
            googleLegacySigninCase.signin(launcher)
        } catch (e: Exception) {
            Log.e("onClickSignInWithGoogleLegacy", "Error!! ${e.localizedMessage}")
            SnackbarManager.showMessage(e.toSnackbarMessage())
        }
    }

    fun onResultSignInWithGoogleLegacy(result: ActivityResult) {
        launchCatching("onResultSignInWithGoogleLegacy") {
            googleLegacySigninCase.onResultSignin(result)
        }
    }
}