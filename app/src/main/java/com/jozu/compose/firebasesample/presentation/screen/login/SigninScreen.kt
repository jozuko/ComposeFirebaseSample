package com.jozu.compose.firebasesample.presentation.screen.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
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
private val spaceSizeLarge: Dp = 16.dp
private val spaceSizeMedium: Dp = 8.dp

@Composable
fun SigninScreen(
    mailLink: String?,
    modifier: Modifier = Modifier,
    viewModel: SigninViewModel = hiltViewModel(),
) {
    viewModel.accountState.collectAsState()
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onReceiveMailLink(mailLink)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.status) {
            SigninUiStatus.Idle -> Text("not working")
            SigninUiStatus.Proceeding -> Text("working...")
            SigninUiStatus.Authorized -> Text("Authorized!! ${uiState.account?.id}")
            SigninUiStatus.Error -> Text("Error!!")
        }

        SignOutButton(uiState, viewModel)
        PasswordSigninButton(uiState, viewModel)
        MailLinkSigninButton(uiState, viewModel)
        GoogleOneTapSigninButton(viewModel)
    }
}

@Composable
private fun ColumnScope.SignOutButton(uiState: SigninUiState, viewModel: SigninViewModel) {
    BasicButton(
        text = R.string.sign_out,
        enabled = uiState.status == SigninUiStatus.Authorized,
        modifier = Modifier
            .padding(horizontal = spaceSizeLarge, vertical = spaceSizeMedium)
            .align(Alignment.End),
        onClick = { viewModel.onSignOutClick() },
    )
}

@Composable
private fun PasswordSigninButton(uiState: SigninUiState, viewModel: SigninViewModel) {
    val focusManager = LocalFocusManager.current

    Card(modifier = Modifier.padding(spaceSizeLarge)) {
        Spacer(modifier = Modifier.height(spaceSizeMedium))

        Text(
            text = stringResource(R.string.sign_mode_password_auth),
            modifier = Modifier.padding(horizontal = spaceSizeMedium),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(modifier = Modifier.height(spaceSizeMedium))

        EmailField(value = uiState.email, onNewValue = viewModel::onChangeEmail, modifier = Modifier.fieldModifier())
        PasswordField(value = uiState.password, onNewValue = viewModel::onChangePassword, modifier = Modifier.fieldModifier())
        AnimatedVisibility(visible = uiState.isCreateUserMode) {
            PasswordConfirmField(value = uiState.passwordConfirm, onNewValue = viewModel::onChangePasswordConfirm, modifier = Modifier.fieldModifier())
        }

        Spacer(modifier = Modifier.height(spaceSizeMedium))

        if (uiState.isCreateUserMode) {
            BasicButton(text = R.string.sign_up, modifier = Modifier.basicButton(), onClick = {
                focusManager.clearFocus()
                viewModel.onSignupClick()
            })
        } else {
            BasicButton(text = R.string.sign_in, modifier = Modifier.basicButton(), onClick = {
                focusManager.clearFocus()
                viewModel.onSigninClick()
            })
        }

        if (uiState.isCreateUserMode) {
            BasicButton(text = R.string.to_sign_in_mode, modifier = Modifier.basicButton(), onClick = viewModel::onClickToSignin)
        } else {
            BasicButton(text = R.string.to_sign_up_mode, modifier = Modifier.basicButton(), onClick = viewModel::onClickToSignup)
        }
    }
}

/**
 * メールリンク認証
 */
@Composable
private fun MailLinkSigninButton(uiState: SigninUiState, viewModel: SigninViewModel) {
    val focusManager = LocalFocusManager.current

    Card(modifier = Modifier.padding(spaceSizeLarge)) {
        Spacer(modifier = Modifier.height(spaceSizeMedium))

        Text(
            text = stringResource(R.string.sign_mode_mail_link_auth),
            modifier = Modifier.padding(horizontal = spaceSizeMedium),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(modifier = Modifier.height(spaceSizeMedium))

        EmailField(value = uiState.mailLinkEmail, onNewValue = viewModel::onChangeMailLinkEmail, modifier = Modifier.fieldModifier())

        Spacer(modifier = Modifier.height(spaceSizeMedium))

        BasicButton(text = R.string.sign_in, modifier = Modifier.basicButton(), onClick = {
            focusManager.clearFocus()
            viewModel.onClickMailLinkSignin()
        })
    }
}

/**
 * Google認証
 *
 * テストでキャンセルしすぎた場合は、「*#*#66382723#*#*」に電話をかけましょう。制限がオフになります。
 * オンに戻すときは同じ番号にもう一度電話をかけましょう
 * https://developers.google.com/identity/one-tap/android/get-saved-credentials?hl=ja#disable-one-tap
 */
@Composable
private fun GoogleOneTapSigninButton(viewModel: SigninViewModel) {
    Card(modifier = Modifier.padding(spaceSizeLarge)) {
        Spacer(modifier = Modifier.height(spaceSizeMedium))

        Text(
            text = stringResource(R.string.sign_mode_google_auth),
            modifier = Modifier.padding(horizontal = spaceSizeMedium),
            style = MaterialTheme.typography.titleSmall,
        )

        Spacer(modifier = Modifier.height(spaceSizeMedium))

        val startForResultOneTap = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = viewModel::onResultSignInWithGoogleOneTap,
        )
        BasicButton(
            text = R.string.sign_in_with_google_one_tap,
            modifier = Modifier.basicButton(),
            onClick = { viewModel.onClickSignInWithGoogleOneTap(startForResultOneTap) },
        )

        val startForResultLegacy = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = viewModel::onResultSignInWithGoogleLegacy,
        )
        BasicButton(
            text = R.string.sign_in_with_google_legacy,
            modifier = Modifier.basicButton(),
            onClick = { viewModel.onClickSignInWithGoogleLegacy(startForResultLegacy) },
        )

        Spacer(modifier = Modifier.height(spaceSizeMedium))
    }
}
