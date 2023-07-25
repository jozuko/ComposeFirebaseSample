package com.jozu.compose.firebasesample.di

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseAuth
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.infra.AccountRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideAccountRepository(
        auth: FirebaseAuth,
        signInRequest: BeginSignInRequest,
    ): AccountRepository = AccountRepositoryImpl(auth, signInRequest)
}