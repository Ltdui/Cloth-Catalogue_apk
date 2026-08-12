package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun AyushTextileLogo(
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    showBrandText: Boolean = true,
    tintColor: Color = Color(0xFF6B0014)
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Official Brand Logo Emblem Icon Container
        Box(
            modifier = Modifier
                .size(logoSize)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF8F5))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ayush_logo),
                contentDescription = "AYUSH TEXTILE Logo",
                modifier = Modifier.size(logoSize * 0.9f)
            )
        }

        if (showBrandText) {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AYUSH TEXTILE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = tintColor,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "— EXCLUSIVE FABRIC CATALOG —",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = tintColor.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
