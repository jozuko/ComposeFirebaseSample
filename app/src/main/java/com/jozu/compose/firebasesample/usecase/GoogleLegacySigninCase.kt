package com.jozu.compose.firebasesample.usecase

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.usecase.model.SigninGoogleLegacyError
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/28.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class GoogleLegacySigninCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    fun signin(launcher: ActivityResultLauncher<Intent>) {
        val intent = accountRepository.requestGoogleLegacyAuth()
        launcher.launch(intent)
    }

    suspend fun onResultSignin(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d("GoogleLegacySigninCase", "onResultSignin ${SigninGoogleLegacyError(result.data).resultMessage}")
            return
        }

        val resultData = result.data ?: let {
            Log.d("GoogleLegacySigninCase", "onResultSignin result.data is null")
            return
        }

        accountRepository.signinGoogleLegacy(resultData)
    }
}