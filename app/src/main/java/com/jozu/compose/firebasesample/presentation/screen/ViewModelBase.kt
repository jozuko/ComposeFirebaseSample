package com.jozu.compose.firebasesample.presentation.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarManager
import com.jozu.compose.firebasesample.presentation.common.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * Created by jozuko on 2023/07/28.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
abstract class ViewModelBase : ViewModel() {
    fun launchCatching(logTag: String, block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(
            context = CoroutineExceptionHandler { _, throwable ->
                Log.e("ViewModel", "[${logTag}]Error ${throwable.localizedMessage}")
                SnackbarManager.showMessage(throwable.toSnackbarMessage())
            },
            block = {
                withContext(Dispatchers.IO) {
                    Log.d("ViewModel", "[${logTag}]Thread => ${Thread.currentThread().name}")
                    block.invoke(this)
                }
            }
        )
    }
}