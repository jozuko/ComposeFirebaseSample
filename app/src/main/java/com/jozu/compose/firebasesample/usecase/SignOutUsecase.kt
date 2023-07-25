package com.jozu.compose.firebasesample.usecase

import android.app.Activity
import com.google.android.gms.auth.api.identity.Identity
import com.jozu.compose.firebasesample.domain.AccountRepository
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/24.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class SignOutUsecase @Inject
constructor(
    private val accountRepository: AccountRepository,
) {
    suspend fun signOut(activity: Activity) {
        accountRepository.signOut(Identity.getSignInClient(activity))
    }
}