package com.jozu.compose.firebasesample.usecase.model

import android.content.Intent
import android.os.Build
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 *
 * Created by jozuko on 2023/07/28.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
data class SigninGoogleLegacyError(val resultData: Intent?) {
    val resultMessage: String
        get() {
            resultData ?: return "result.data is null"

            if (resultData.hasExtra("googleSignInStatus")) {
                val googleSigninStatus: Status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    resultData.getParcelableExtra("googleSignInStatus", Status::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    resultData.getParcelableExtra("googleSignInStatus")
                } ?: return "result.data[googleSignInStatus] is null"

                // https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInStatusCodes
                val statusCode = when (googleSigninStatus.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                        "GoogleSignInStatusCodes.SIGN_IN_CANCELLED(${googleSigninStatus.statusCode})"
                    }

                    GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> {
                        "GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS(${googleSigninStatus.statusCode})"
                    }

                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                        "GoogleSignInStatusCodes.SIGN_IN_FAILED(${googleSigninStatus.statusCode})"
                    }

                    GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> {
                        "CommonStatusCodes.SIGN_IN_REQUIRED(${googleSigninStatus.statusCode})"
                    }

                    CommonStatusCodes.NETWORK_ERROR -> {
                        "CommonStatusCodes.NETWORK_ERROR(${googleSigninStatus.statusCode})"
                    }

                    CommonStatusCodes.INVALID_ACCOUNT -> {
                        "CommonStatusCodes.INVALID_ACCOUNT(${googleSigninStatus.statusCode})"
                    }

                    CommonStatusCodes.INTERNAL_ERROR -> {
                        "CommonStatusCodes.INTERNAL_ERROR(${googleSigninStatus.statusCode})"
                    }

                    else -> {
                        "UNKNOWN(${googleSigninStatus.statusCode})"
                    }
                }

                return "statusCode=$statusCode"
            } else {
                return "result.data has not googleSignInStatus"
            }
        }
}