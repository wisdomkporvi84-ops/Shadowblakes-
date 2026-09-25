package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSecurity
import com.example.ui.theme.RedAlert
import com.example.ui.theme.RedContainer

@Composable
fun InventoryBadge(
    stock: Int,
    initialStock: Int = 50,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    when {
        stock <= 0 -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(RedContainer.copy(alpha = 0.6f))
                    .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(RedAlert)
                )
                Text(
                    text = "Out of Stock",
                    color = RedAlert,
                    fontSize = if (compact) 10.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        stock <= 5 -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF7C2D12).copy(alpha = 0.4f))
                    .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(Color(0xFFF97316))
                )
                Text(
                    text = if (compact) "Only $stock left" else "Low Stock: Only $stock units left!",
                    color = Color(0xFFFB923C),
                    fontSize = if (compact) 10.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        else -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(EmeraldContainer.copy(alpha = 0.4f))
                    .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldSecurity)
                )
                Text(
                    text = if (compact) "$stock in stock" else "Real-time Stock: $stock available",
                    color = EmeraldSecurity,
                    fontSize = if (compact) 10.sp else 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
