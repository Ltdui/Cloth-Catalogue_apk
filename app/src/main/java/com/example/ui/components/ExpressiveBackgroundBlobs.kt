package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

@Composable
fun ExpressiveBackgroundBlobs(
    modifier: Modifier = Modifier,
    alpha: Float = 0.25f
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Top-right soft primary blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = alpha * 0.8f),
                        primaryColor.copy(alpha = alpha * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.85f, height * 0.12f),
                    radius = width * 0.45f
                ),
                center = Offset(width * 0.85f, height * 0.12f),
                radius = width * 0.45f
            )

            // Top-left tertiary soft blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tertiaryColor.copy(alpha = alpha * 0.6f),
                        tertiaryColor.copy(alpha = alpha * 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.1f, height * 0.28f),
                    radius = width * 0.35f
                ),
                center = Offset(width * 0.1f, height * 0.28f),
                radius = width * 0.35f
            )

            // Bottom-right secondary blob
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryColor.copy(alpha = alpha * 0.7f),
                        secondaryColor.copy(alpha = alpha * 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(width * 0.9f, height * 0.82f),
                    radius = width * 0.5f
                ),
                center = Offset(width * 0.9f, height * 0.82f),
                radius = width * 0.5f
            )

            // Subtle organic path overlay in bottom left
            val path = Path().apply {
                moveTo(0f, height * 0.7f)
                cubicTo(
                    width * 0.25f, height * 0.62f,
                    width * 0.4f, height * 0.78f,
                    width * 0.2f, height
                )
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = path,
                color = primaryColor.copy(alpha = alpha * 0.12f)
            )
        }
    }
}
