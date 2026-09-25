package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsiaHouseTopBar(
    cartCount: Int,
    alertCount: Int,
    onCartClick: () -> Unit,
    onAlertsClick: () -> Unit,
    onSearchClick: () -> Unit,
    title: String = "Asia House"
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(OrangePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AH",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Premium Phone & Laptop Gear",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Search button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("top_bar_search_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Accessories",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Real-time Inventory Alerts Notification
            IconButton(
                onClick = onAlertsClick,
                modifier = Modifier.testTag("top_bar_alerts_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (alertCount > 0) {
                            Badge(
                                containerColor = OrangePrimary,
                                contentColor = Color.White
                            ) {
                                Text(text = alertCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Live Inventory Alerts",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Shopping Cart with badge
            IconButton(
                onClick = onCartClick,
                modifier = Modifier.testTag("top_bar_cart_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = OrangePrimary,
                                contentColor = Color.White
                            ) {
                                Text(text = cartCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shopping Cart",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
