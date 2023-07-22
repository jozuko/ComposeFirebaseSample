package com.jozu.compose.firebasesample.domain

import com.google.firebase.auth.FirebaseUser

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
data class Account(
    val id: String,
    val isAnonymous: Boolean,
) {
    companion object Builder {
        fun fromFirebaseUser(user: FirebaseUser): Account {
            return Account(
                id = user.uid,
                isAnonymous = user.isAnonymous,
            )
        }
    }
}