package com.jozu.compose.firebasesample.infra

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 *
 * Created by jozuko on 2023/07/28.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class SharedPref(
    private val sharedPreferences: SharedPreferences,
) {
    var mailLinkAddress: String
        get() = sharedPreferences.getString(SharedPrefKey.MAIL_LINK_ADDRESS.name, null) ?: ""
        set(value) = sharedPreferences.edit {
            putString(SharedPrefKey.MAIL_LINK_ADDRESS.name, value)
        }
}