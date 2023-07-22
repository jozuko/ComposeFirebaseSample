package com.jozu.compose.firebasesample.infra

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.jozu.compose.firebasesample.domain.Account
import com.jozu.compose.firebasesample.domain.AccountFuture
import com.jozu.compose.firebasesample.domain.AccountRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class AccountRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AccountRepository {
    override val accountFuture: Flow<AccountFuture<Account>>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                Log.d("AccountRepository", "AuthStateListener called")
                this.trySend(AccountFuture.fromFirebaseUser(auth.currentUser))
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun createAnonymousAccount() {
        Log.d("AccountRepository", "createAnonymousAccount start")
        val authResult = auth.signInAnonymously().await()
        Log.d("AccountRepository", "createAnonymousAccount ${authResult.user?.uid}")
    }

    override suspend fun signin(email: String, password: String) {
        Log.d("AccountRepository", "signin start")
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        Log.d("AccountRepository", "signin ${authResult.user?.uid}")
    }

    override suspend fun signup(email: String, password: String) {
        Log.d("AccountRepository", "signup start")
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isAnonymous) {
            val credential = EmailAuthProvider.getCredential(email, password)
            val authResult = currentUser.linkWithCredential(credential).await()
            Log.d("AccountRepository", "signup linkWithCredential ${authResult.user?.uid}")
        } else {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("AccountRepository", "signup ${authResult.user?.uid}")
        }
    }
}