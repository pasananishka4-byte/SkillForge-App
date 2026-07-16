package com.skillforge.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skillforge.app.ui.theme.*

data class ChartDataPoint(
    val label: String,
    val value: Float
)

@Composable
fun RadarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    maxValue: Float = 10f,
    primaryColor: Color = Primary,
    backgroundColor: Color = SurfaceVariant
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "radar"
    )

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 - 40.dp.toPx()
        val angleStep = (2 * Math.PI / data.size).toFloat()

        // Draw background circles
        for (i in 1..5) {
            val circleRadius = radius * i / 5
            drawCircle(
                color = backgroundColor,
                radius = circleRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes
        data.forEachIndexed { index, _ ->
            val angle = angleStep * index - Math.PI.toFloat() / 2
            val endX = center.x + radius * kotlin.math.cos(angle)
            val endY = center.y + radius * kotlin.math.sin(angle)
            drawLine(
                color = backgroundColor,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw data polygon
        val path = Path()
        data.forEachIndexed { index, (_, value) ->
            val angle = angleStep * index - Math.PI.toFloat() / 2
            val normalizedValue = (value / maxValue).coerceIn(0f, 1f) * animatedProgress
            val pointRadius = radius * normalizedValue
            val x = center.x + pointRadius * kotlin.math.cos(angle)
            val y = center.y + pointRadius * kotlin.math.sin(angle)

            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        path.close()

        // Fill
        drawPath(path, primaryColor.copy(alpha = 0.2f))
        // Stroke
        drawPath(path, primaryColor, style = Stroke(width = 2.dp.toPx()))

        // Draw points and labels
        data.forEachIndexed { index, (label, value) ->
            val angle = angleStep * index - Math.PI.toFloat() / 2
            val normalizedValue = (value / maxValue).coerceIn(0f, 1f) * animatedProgress
            val pointRadius = radius * normalizedValue
            val x = center.x + pointRadius * kotlin.math.cos(angle)
            val y = center.y + pointRadius * kotlin.math.sin(angle)

            drawCircle(
                color = primaryColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )

            // Label
            val labelRadius = radius + 20.dp.toPx()
            val labelX = center.x + labelRadius * kotlin.math.cos(angle)
            val labelY = center.y + labelRadius * kotlin.math.sin(angle)

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#B0B0B0")
                    textSize = 10.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                drawText(label, labelX, labelY + 4.dp.toPx(), paint)
            }
        }
    }
}

@Composable
fun BarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    barColor: Color = Primary,
    maxValue: Float? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "bar"
    )

    val maxVal = maxValue ?: (data.maxOfOrNull { it.value } ?: 1f)

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val barWidth = (size.width - (data.size - 1) * 8.dp.toPx()) / data.size
        val chartHeight = size.height - 20.dp.toPx()

        data.forEachIndexed { index, dataPoint ->
            val barHeight = if (maxVal > 0) (dataPoint.value / maxVal) * chartHeight * animatedProgress else 0f
            val x = index * (barWidth + 8.dp.toPx())

            // Bar
            drawRect(
                color = barColor.copy(alpha = 0.7f),
                topLeft = Offset(x, chartHeight - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )

            // Label
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#B0B0B0")
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
                drawText(
                    dataPoint.label,
                    x + barWidth / 2,
                    size.height,
                    paint
                )
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = Primary,
    fillColor: Color = Primary.copy(alpha = 0.1f)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "line"
    )

    val maxValue = data.maxOfOrNull { it.value } ?: 1f

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        val chartHeight = size.height - 20.dp.toPx()

        // Fill path
        val fillPath = Path()
        data.forEachIndexed { index, dataPoint ->
            val x = index * stepX
            val y = chartHeight - (dataPoint.value / maxValue) * chartHeight * animatedProgress

            if (index == 0) {
                fillPath.moveTo(x, y)
            } else {
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo(size.width, chartHeight)
        fillPath.lineTo(0f, chartHeight)
        fillPath.close()
        drawPath(fillPath, fillColor)

        // Line path
        val linePath = Path()
        data.forEachIndexed { index, dataPoint ->
            val x = index * stepX
            val y = chartHeight - (dataPoint.value / maxValue) * chartHeight * animatedProgress

            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
        }
        drawPath(linePath, lineColor, style = Stroke(width = 2.dp.toPx()))

        // Points
        data.forEachIndexed { index, dataPoint ->
            val x = index * stepX
            val y = chartHeight - (dataPoint.value / maxValue) * chartHeight * animatedProgress

            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(x, y))
        }
    }
}
