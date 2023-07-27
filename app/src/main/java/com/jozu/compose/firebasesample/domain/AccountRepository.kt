package com.jozu.compose.firebasesample.domain

import android.app.PendingIntent
import android.content.Intent
import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
interface AccountRepository {
    val accountFuture: Flow<AccountFuture<Account>>

    suspend fun createAnonymousAccount()
    suspend fun signinMail(email: String, password: String)
    suspend fun signupMail(email: String, password: String)
    suspend fun signOut()
    suspend fun requestGoogleOneTapAuth(): PendingIntent
    suspend fun signinGoogleOneTapAuth(resultData: Intent)
    fun requestGoogleLegacyAuth(): Intent
    suspend fun signinGoogleLegacy(resultData: Intent)
}