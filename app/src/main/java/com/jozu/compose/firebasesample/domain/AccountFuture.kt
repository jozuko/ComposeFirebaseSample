package com.jozu.compose.firebasesample.domain

import com.google.firebase.auth.FirebaseUser

/**
 *
 * Created by jozuko on 2023/07/22.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
sealed class AccountFuture<out Account> {
    object Idle : AccountFuture<Nothing>()
    data class Authorized(val account: Account) : AccountFuture<Account>()

    companion object {
        fun fromFirebaseUser(user: FirebaseUser?): AccountFuture<Account> {
            return user?.let {
                Authorized(Account.fromFirebaseUser(it))
            } ?: Idle
        }
    }
}