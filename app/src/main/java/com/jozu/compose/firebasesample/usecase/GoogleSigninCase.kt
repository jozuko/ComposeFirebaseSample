package com.jozu.compose.firebasesample.usecase

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.usecase.model.SigninOneTapError
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/24.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class GoogleSigninCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend fun signinOneTap(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        val signInClient = Identity.getSignInClient(activity)
        val pendingIntent = accountRepository.requestGoogleOneTapAuth(signInClient)
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        launcher.launch(intentSenderRequest)
    }

    suspend fun onResultSigninOneTap(activity: Activity, result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d("SigninViewModel", "onResultGoogleSignIn ${SigninOneTapError(result.data).resultMessage}")
            return
        }

        val resultData = result.data ?: let {
            Log.d("SigninViewModel", "onResultGoogleSignIn result.data is null")
            return
        }

        val signInClient = Identity.getSignInClient(activity)
        accountRepository.signinGoogleOneTapAuth(signInClient, resultData)
    }
}