package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.components.SecurityGuaranteesRow
import com.example.ui.components.rememberProductImageRes
import com.example.ui.theme.EmeraldSecurity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val secondsRemaining by viewModel.reservationSecondsRemaining.collectAsState()
    val promoCode by viewModel.appliedPromoCode.collectAsState()
    val promoDiscountPercent by viewModel.promoDiscountPercent.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var enteredPromo by remember { mutableStateOf("") }
    var promoError by remember { mutableStateOf<String?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val subtotal = cartItems.sumOf { it.product.price * it.quantity }
    val discount = subtotal * (promoDiscountPercent / 100.0)
    val shipping = if (subtotal > 99.0 || subtotal == 0.0) 0.0 else 9.99
    val total = (subtotal - discount + shipping).coerceAtLeast(0.0)
    val pointsEarned = (total * 5).toInt()

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timerString = "%02d:%02d".format(minutes, seconds)

    if (showCheckoutDialog) {
        CheckoutDialog(
            viewModel = viewModel,
            totalAmount = total,
            deliveryAddress = userProfile.defaultAddress,
            onDismiss = { showCheckoutDialog = false },
            onOrderPlaced = {
                showCheckoutDialog = false
            }
        )
    }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Explore our premium phone & laptop accessories and take advantage of real-time inventory deals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.navigateTo(ScreenDestination.Catalog) },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_cart_shop_btn")
                ) {
                    Text("Browse Accessories", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total (Incl. Tax)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$${"%.2f".format(total)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("cart_proceed_checkout_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Secure Checkout",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Secure Checkout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("cart_screen_column"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Stock Reservation Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, EmeraldSecurity.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = "Timer",
                            tint = EmeraldSecurity,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Real-Time Stock Reserved:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = timerString,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldSecurity
                                )
                            }
                            Text(
                                text = "These units are locked for you. Complete payment before the timer releases them.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Cart Items List
            items(cartItems, key = { it.id }) { item ->
                CartItemRow(
                    cartItem = item,
                    onIncrease = {
                        if (item.quantity < item.product.stock) {
                            viewModel.updateCartQuantity(item.id, item.quantity + 1)
                        }
                    },
                    onDecrease = {
                        viewModel.updateCartQuantity(item.id, item.quantity - 1)
                    },
                    onDelete = {
                        viewModel.removeCartItem(item.id)
                    }
                )
            }

            // Promo Code Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Promo & Voucher Code",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (promoCode != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(OrangePrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Code '$promoCode' applied ($promoDiscountPercent% off)",
                                        color = OrangePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = "Remove",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.removePromoCode() }
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = enteredPromo,
                                    onValueChange = {
                                        enteredPromo = it
                                        promoError = null
                                    },
                                    placeholder = { Text("e.g. ASIA15") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .testTag("cart_promo_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        if (viewModel.applyPromoCode(enteredPromo)) {
                                            enteredPromo = ""
                                            promoError = null
                                        } else {
                                            promoError = "Invalid code. Try ASIA15 or TECHVIP"
                                        }
                                    },
                                    modifier = Modifier
                                        .height(50.dp)
                                        .testTag("cart_apply_promo_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (promoError != null) {
                                Text(
                                    text = promoError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Available promo:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                SuggestionChip(
                                    onClick = {
                                        viewModel.applyPromoCode("ASIA15")
                                    },
                                    label = { Text("ASIA15 (-15%)", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = {
                                        viewModel.applyPromoCode("TECHVIP")
                                    },
                                    label = { Text("TECHVIP (-20%)", fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Order Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        SummaryLine("Subtotal (${cartItems.sumOf { it.quantity }} items)", "$${"%.2f".format(subtotal)}")

                        if (discount > 0) {
                            SummaryLine(
                                label = "Promo Discount ($promoDiscountPercent%)",
                                value = "-$${"%.2f".format(discount)}",
                                highlight = true
                            )
                        }

                        SummaryLine(
                            label = "Express Courier Shipping",
                            value = if (shipping == 0.0) "FREE (Orders > $99)" else "$${"%.2f".format(shipping)}"
                        )

                        SummaryLine("Eco-Friendly Packaging & Insurance", "FREE")

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${"%.2f".format(total)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = OrangePrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Earn +$pointsEarned Asia House Points on this purchase",
                                fontSize = 12.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Security Row
            item {
                SecurityGuaranteesRow()
            }
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    val imageRes = rememberProductImageRes(cartItem.product.imageResName, cartItem.product.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cart_item_${cartItem.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = cartItem.product.title,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = cartItem.product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "Finish: ${cartItem.selectedColor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${"%.2f".format(cartItem.product.price)} each",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (cartItem.product.stock <= 5) {
                    Text(
                        text = "Real-time stock: Only ${cartItem.product.stock} units left",
                        fontSize = 10.sp,
                        color = Color(0xFFFB923C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quantity stepper & Delete
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Remove item",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${cartItem.quantity}",
                        modifier = Modifier.padding(horizontal = 6.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    IconButton(
                        onClick = onIncrease,
                        enabled = cartItem.quantity < cartItem.product.stock,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (highlight) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = if (highlight) OrangePrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
