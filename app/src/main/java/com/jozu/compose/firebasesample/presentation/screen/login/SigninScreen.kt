package com.jozu.compose.firebasesample.presentation.screen.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jozu.compose.firebasesample.R
import com.jozu.compose.firebasesample.presentation.common.composable.BasicButton
import com.jozu.compose.firebasesample.presentation.common.composable.EmailField
import com.jozu.compose.firebasesample.presentation.common.composable.PasswordConfirmField
import com.jozu.compose.firebasesample.presentation.common.composable.PasswordField
import com.jozu.compose.firebasesample.presentation.common.ext.basicButton
import com.jozu.compose.firebasesample.presentation.common.ext.fieldModifier

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
@Composable
fun SigninScreen(
    modifier: Modifier = Modifier,
    viewModel: SigninViewModel = hiltViewModel(),
) {
    viewModel.accountState.collectAsState()
    val uiState by viewModel.uiState
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.status) {
            SigninUiStatus.Idle -> Text("not working")
            SigninUiStatus.Proceeding -> Text("working...")
            SigninUiStatus.Authorized -> Text("Authorized!! ${uiState.account?.id}")
            SigninUiStatus.Error -> Text("Error!!")
        }

        EmailField(value = uiState.email, onNewValue = viewModel::onEmailChange, modifier = Modifier.fieldModifier())
        PasswordField(value = uiState.password, onNewValue = viewModel::onPasswordChange, modifier = Modifier.fieldModifier())
        AnimatedVisibility(visible = uiState.isCreateUserMode) {
            PasswordConfirmField(value = uiState.passwordConfirm, onNewValue = viewModel::onPasswordConfirmChange, modifier = Modifier.fieldModifier())
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isCreateUserMode) {
            BasicButton(text = R.string.sign_up, modifier = Modifier.basicButton(), action = {
                focusManager.clearFocus()
                viewModel.onSignupClick()
            })
        } else {
            BasicButton(text = R.string.sign_in, modifier = Modifier.basicButton(), action = {
                focusManager.clearFocus()
                viewModel.onSigninClick()
            })
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isCreateUserMode) {
            BasicButton(text = R.string.to_sign_in_mode, modifier = Modifier.basicButton(), action = viewModel::onToSigninClick)
        } else {
            BasicButton(text = R.string.to_sign_up_mode, modifier = Modifier.basicButton(), action = viewModel::onToSignupClick)
        }
    }
}