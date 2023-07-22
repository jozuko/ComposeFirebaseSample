package com.jozu.compose.firebasesample.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
object FirebaseModule {
    @Provides
    fun auth(): FirebaseAuth = Firebase.auth
}