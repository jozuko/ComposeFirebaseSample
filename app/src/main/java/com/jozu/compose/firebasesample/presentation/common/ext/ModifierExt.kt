package com.jozu.compose.firebasesample.presentation.common.ext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 *
 * Created by jozuko on 2023/07/21.
 * Copyright (c) 2023 Studio Jozu. All rights reserved.
 */
fun Modifier.fieldModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
}

fun Modifier.basicButton(): Modifier {
    return this
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
}
