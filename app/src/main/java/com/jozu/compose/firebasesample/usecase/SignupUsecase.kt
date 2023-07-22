package com.jozu.compose.firebasesample.usecase

import com.jozu.compose.firebasesample.domain.AccountRepository
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/22.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class SignupUsecase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend fun signup(email: String, password: String) {
        accountRepository.signup(email, password)
    }
}