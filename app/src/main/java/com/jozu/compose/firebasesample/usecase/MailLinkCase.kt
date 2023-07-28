package com.jozu.compose.firebasesample.usecase

import com.jozu.compose.firebasesample.domain.AccountRepository
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/28.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class MailLinkCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    suspend fun sendMailLink(email: String) {
        accountRepository.sendMailLinkSignInMail(email)
    }

    suspend fun signinMailLink(mailLink: String?) {
        accountRepository.signinMailLink(mailLink)
    }
}