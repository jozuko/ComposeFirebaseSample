package com.jozu.compose.firebasesample.usecase

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.jozu.compose.firebasesample.domain.AccountRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/24.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class SignOutUsecase @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val accountRepository: AccountRepository,
) {
    suspend fun signOut() {
        accountRepository.signOut(Identity.getSignInClient(context))
    }
}