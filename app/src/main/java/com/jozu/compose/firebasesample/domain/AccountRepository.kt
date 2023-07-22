package com.jozu.compose.firebasesample.domain

import kotlinx.coroutines.flow.Flow

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
interface AccountRepository {
    val accountFuture: Flow<AccountFuture<Account>>

    suspend fun createAnonymousAccount()
    suspend fun signin(email: String, password: String)
    suspend fun signup(email: String, password: String)
}