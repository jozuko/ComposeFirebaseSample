package com.jozu.compose.firebasesample.di

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.jozu.compose.firebasesample.domain.AccountRepository
import com.jozu.compose.firebasesample.infra.AccountRepositoryImpl
import com.jozu.compose.firebasesample.infra.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideSharedPref(
        @ApplicationContext context: Context,
    ): SharedPref {
        val sharedPreferences = context.getSharedPreferences("firebase_sample_pref", Context.MODE_PRIVATE)
        return SharedPref(sharedPreferences)
    }

    @Provides
    fun provideAccountRepository(
        @ApplicationContext context: Context,
        auth: FirebaseAuth,
        beginSignInRequest: BeginSignInRequest,
        signInClient: SignInClient,
        googleSignInClient: GoogleSignInClient,
        sharedPref: SharedPref,
    ): AccountRepository = AccountRepositoryImpl(context, auth, beginSignInRequest, signInClient, googleSignInClient, sharedPref)
}