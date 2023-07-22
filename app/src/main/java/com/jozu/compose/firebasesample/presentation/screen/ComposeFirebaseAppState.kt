package com.jozu.compose.firebasesample.presentation.screen

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarManager
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
@Stable
class ComposeFirebaseAppState(
    val snackbarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    private val coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text)
            }
        }
    }
}