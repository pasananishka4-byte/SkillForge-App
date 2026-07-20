package com.skillforge.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skillforge.app.ui.theme.CardGlow
import com.skillforge.app.ui.theme.GlassBorder
import com.skillforge.app.ui.theme.GlassEnd
import com.skillforge.app.ui.theme.GlassStart
import com.skillforge.app.ui.theme.OnBackground
import com.skillforge.app.ui.theme.OnSurfaceVariant
import com.skillforge.app.ui.theme.Surface

@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    gradient: Brush? = null,
    glassEffect: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    val bgModifier = if (glassEffect) {
        val glassBrush = Brush.verticalGradient(listOf(GlassStart, GlassEnd))
        Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(glassBrush)
            .border(1.dp, GlassBorder, MaterialTheme.shapes.medium)
            .alpha(0.95f)
    } else if (gradient != null) {
        Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(gradient)
    } else {
        Modifier
    }

    val finalModifier = modifier
        .then(if (onClick != null) {
            Modifier
                .scale(scale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
        } else Modifier)

    Box(modifier = finalModifier) {
        if (gradient == null && !glassEffect) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Surface),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().then(bgModifier)) {
                    content()
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().then(bgModifier).padding(1.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = 0.85f)),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
            if (action != null) {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    action()
                }
            }
        }
    }
}
