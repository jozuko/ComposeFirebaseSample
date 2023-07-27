package com.jozu.compose.firebasesample.usecase

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.usecase.model.SigninGoogleOneTapError
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/24.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class GoogleOneTapSigninCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend fun signin(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        val pendingIntent = accountRepository.requestGoogleOneTapAuth()
        val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
        launcher.launch(intentSenderRequest)
    }

    suspend fun onResultSignin(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d("GoogleOneTapSigninCase", "onResultSignin ${SigninGoogleOneTapError(result.data).resultMessage}")
            return
        }

        val resultData = result.data ?: let {
            Log.d("GoogleOneTapSigninCase", "onResultSignin result.data is null")
            return
        }

        accountRepository.signinGoogleOneTapAuth(resultData)
    }
}