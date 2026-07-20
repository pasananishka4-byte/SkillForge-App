package com.skillforge.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.skillforge.app.ui.theme.Background
import com.skillforge.app.ui.theme.Surface

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Background,
        Surface.copy(alpha = 0.5f),
        Background
    ),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = colors,
                    center = Offset(0.5f, 0.3f),
                    radius = 1.2f
                )
            )
    ) {
        content()
    }
}
