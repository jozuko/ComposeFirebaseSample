package com.jozu.compose.firebasesample.infra

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
    private val signInRequest: BeginSignInRequest,
) : AccountRepository {
    override val accountFuture: Flow<AccountFuture<Account>>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                Log.d("AccountRepository", "AuthStateListener called")
                auth.currentUser?.providerData?.forEach { userInfo ->
                    Log.d("AccountRepository", "AuthStateListener userInfo.providerId=${userInfo.providerId}")
                }

                val accountFuture = AccountFuture.fromFirebaseUser(auth.currentUser)
                Log.d("AccountRepository", "AuthStateListener accountFuture=${accountFuture}")
                this.trySend(accountFuture)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun createAnonymousAccount() {
        Log.d("AccountRepository", "createAnonymousAccount start")
        val authResult = auth.signInAnonymously().await()
        Log.d("AccountRepository", "createAnonymousAccount ${authResult.user?.uid}")
    }

    override suspend fun signinMail(email: String, password: String) {
        Log.d("AccountRepository", "signin start")
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        Log.d("AccountRepository", "signin ${authResult.user?.uid}")
    }

    /**
     * output
     * AuthStateListener userInfo.providerId=firebase
     * AuthStateListener userInfo.providerId=password
     */
    override suspend fun signupMail(email: String, password: String) {
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

    override suspend fun signOut(signInClient: SignInClient) {
        val firebaseUser = auth.currentUser ?: return
        if (firebaseUser.providerData.any { it.providerId == "google.com" }) {
            Log.d("AccountRepository", "signInClient.signOut() call")
            signInClient.signOut().await()
        }

        Log.d("AccountRepository", "auth.signOut call")
        auth.signOut()
    }

    override suspend fun requestGoogleOneTapAuth(signInClient: SignInClient): PendingIntent {
        Log.d("AccountRepository", "requestGoogleOneTapAuth start")
        val result = signInClient.beginSignIn(signInRequest).await()
        return result.pendingIntent
    }

    /**
     * output
     * AuthStateListener userInfo.providerId=firebase
     * AuthStateListener userInfo.providerId=google.com
     */
    override suspend fun signinGoogleOneTapAuth(signInClient: SignInClient, resultData: Intent) {
        Log.d("AccountRepository", "signinGoogleOneTapAuth start")

        val googleCredential = signInClient.getSignInCredentialFromIntent(resultData)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.googleIdToken, null)
        val authResult: AuthResult = auth.signInWithCredential(firebaseCredential).await()

        authResult.user?.let { firebaseUser ->
            firebaseUser.providerData.forEach { userInfo ->
                Log.d("AccountRepository", "signinGoogleOneTapAuth userInfo.providerId=${userInfo.providerId}")
            }
        }

        Log.d("AccountRepository", "signinGoogleOneTapAuth ${authResult.user?.uid}")
    }
}