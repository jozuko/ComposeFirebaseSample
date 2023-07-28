package com.jozu.compose.firebasesample.infra

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.actionCodeSettings
import com.jozu.compose.firebasesample.domain.Account
import com.jozu.compose.firebasesample.domain.AccountFuture
import com.jozu.compose.firebasesample.domain.AccountRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
class AccountRepositoryImpl(
    private val applicationContext: Context,
    private val auth: FirebaseAuth,
    private val signInRequest: BeginSignInRequest,
    private val signInClient: SignInClient,
    private val googleSignInClient: GoogleSignInClient,
    private val sharedPref: SharedPref,
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

    override suspend fun signOut() {
        val firebaseUser = auth.currentUser ?: return
        if (firebaseUser.providerData.any { it.providerId == "google.com" }) {
            Log.d("AccountRepository", "signInClient.signOut() call")
            signInClient.signOut().await()
        }

        Log.d("AccountRepository", "auth.signOut call")
        auth.signOut()
    }

    override suspend fun requestGoogleOneTapAuth(): PendingIntent {
        Log.d("AccountRepository", "requestGoogleOneTapAuth start")
        val result = signInClient.beginSignIn(signInRequest).await()
        return result.pendingIntent
    }

    /**
     * output
     * AuthStateListener userInfo.providerId=firebase
     * AuthStateListener userInfo.providerId=google.com
     */
    override suspend fun signinGoogleOneTapAuth(resultData: Intent) {
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

    override fun requestGoogleLegacyAuth(): Intent {
        return googleSignInClient.signInIntent
    }

    override suspend fun signinGoogleLegacy(resultData: Intent) {
        val googleSignInAccount: GoogleSignInAccount = GoogleSignIn.getSignedInAccountFromIntent(resultData).await()
        Log.d("AccountRepository", "signinGoogle googleSignInAccount.idToken=${googleSignInAccount.idToken}")
        val firebaseCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        val authResult: AuthResult = auth.signInWithCredential(firebaseCredential).await()

        authResult.user?.let { firebaseUser ->
            firebaseUser.providerData.forEach { userInfo ->
                Log.d("AccountRepository", "signinGoogle userInfo.providerId=${userInfo.providerId}")
            }
        }

        Log.d("AccountRepository", "signinGoogle ${authResult.user?.uid}")
    }

    override suspend fun sendMailLinkSignInMail(email: String) {
        val actionCodeSettings = actionCodeSettings {
            url = "https://qiita.com/jozuko_dev"
            handleCodeInApp = true
            setAndroidPackageName(
                /* package-name */applicationContext.packageName,
                /* installIfNotAvailable */true,
                /* minimumVersion */null,
            )
        }

        auth.sendSignInLinkToEmail(email, actionCodeSettings).await()

        sharedPref.mailLinkAddress = email
    }

    override suspend fun signinMailLink(mailLink: String?) {
        val email = sharedPref.mailLinkAddress
        if (mailLink != null) {
            if (email.isEmpty()) {
                throw IllegalArgumentException("not saved e-mail address.")
            }
            auth.signInWithEmailLink(email, mailLink).await()
        }
        sharedPref.mailLinkAddress = ""
    }
}