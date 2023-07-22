package com.jozu.compose.firebasesample.presentation.screen.login

import android.util.Log
import androidx.annotation.StringRes
import com.jozu.compose.firebasesample.R
import com.jozu.compose.firebasesample.domain.Account
import com.jozu.compose.firebasesample.domain.AccountFuture
import com.jozu.compose.firebasesample.presentation.common.ext.isValidEmail
import com.jozu.compose.firebasesample.presentation.common.ext.isValidPassword

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
data class SigninUiState(
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val isCreateUserMode: Boolean,
    val status: SigninUiStatus,
    val account: Account?,
) {
    companion object {
        val initial
            get() = SigninUiState(
                email = "",
                password = "",
                passwordConfirm = "",
                isCreateUserMode = false,
                status = SigninUiStatus.Idle,
                account = null,
            )
    }

    fun updateCurrentUser(accountFuture: AccountFuture<Account>): SigninUiState {
        Log.d("SigninUiState", "$accountFuture")
        if (accountFuture is AccountFuture.Authorized) {
            return this.copy(
                status = SigninUiStatus.Authorized,
                account = accountFuture.account,
            )
        }
        return this
    }

    @StringRes
    fun validateSignin(): Int {
        if (!email.isValidEmail()) {
            return R.string.email_invalid
        }
        if (!password.isValidPassword()) {
            Log.d("SigninUiState", "validateSignin password=[$password]")
            return R.string.password_invalid
        }
        return 0
    }

    @StringRes
    fun validateSignup(): Int {
        if (!email.isValidEmail()) {
            return R.string.email_invalid
        }
        if (!password.isValidPassword()) {
            return R.string.password_invalid
        }
        if (!passwordConfirm.isValidPassword()) {
            return R.string.password_invalid
        }
        if (password != passwordConfirm) {
            return R.string.password_not_equals
        }
        return 0
    }

    fun doSignProcess(): SigninUiState {
        return this.copy(
            status = SigninUiStatus.Proceeding,
            account = null,
        )
    }
}

enum class SigninUiStatus {
    Idle,
    Proceeding,
    Authorized,
    Error,
}