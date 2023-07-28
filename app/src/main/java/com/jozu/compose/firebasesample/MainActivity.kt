package com.jozu.compose.firebasesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.jozu.compose.firebasesample.presentation.screen.ComposeFirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { linkData ->
                val mailLink = linkData?.link?.toString()
                if (mailLink != null && Firebase.auth.isSignInWithEmailLink(mailLink)) {
                    setContent { ComposeFirebaseApp(mailLink = mailLink) }
                } else {
                    setContent { ComposeFirebaseApp() }
                }
            }
            .addOnFailureListener {
                setContent { ComposeFirebaseApp() }
            }
    }
}

