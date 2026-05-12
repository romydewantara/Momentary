package com.example.momentary.ui.screen.home.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.momentary.R

@Composable
fun ScrollToTopButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = stringResource(R.string.home_scroll_to_top),
        )
    }
}
