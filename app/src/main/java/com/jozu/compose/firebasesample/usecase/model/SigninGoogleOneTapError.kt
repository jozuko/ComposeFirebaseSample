package com.jozu.compose.firebasesample.usecase.model

import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts

/**
 *
 * Created by jozuko on 2023/07/24.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
data class SigninGoogleOneTapError(val resultData: Intent?) {
    val resultMessage: String
        get() {
            resultData ?: return "result.data is null"

            if (resultData.action != ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST) {
                return "result.data.action ${resultData.action}"
            }

            val exception: Exception? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resultData.getSerializableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION, Exception::class.java)
            } else {
                @Suppress("DEPRECATION")
                resultData.getSerializableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION) as? Exception
            }

            return "Couldn't start One Tap UI: ${exception?.localizedMessage}"
        }
}