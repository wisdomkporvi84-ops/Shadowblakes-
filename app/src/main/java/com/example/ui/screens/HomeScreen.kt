package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.InventoryBadge
import com.example.ui.components.ProductCard
import com.example.ui.components.SecurityGuaranteesRow
import com.example.ui.components.rememberProductImageRes
import com.example.ui.theme.EmeraldSecurity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val alerts by viewModel.inventoryAlerts.collectAsState()

    val context = LocalContext.current
    val heroResId = context.resources.getIdentifier("hero_asia_house", "drawable", context.packageName).let {
        if (it != 0) it else com.example.R.drawable.ic_launcher_foreground
    }

    val lowStockProducts = products.filter { it.isLowStock }
    val featuredPhoneProducts = products.filter { it.category == "Phone Accessories" }
    val featuredLaptopProducts = products.filter { it.category == "Laptop Accessories" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("home_hero_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = heroResId),
                        contentDescription = "Asia House Flagship",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF090D16).copy(alpha = 0.85f),
                                        Color(0xFF090D16).copy(alpha = 0.98f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(OrangePrimary)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OFFICIAL STORE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "• Real-Time Inventory Active",
                                color = EmeraldSecurity,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Precision Accessories for Phone & Laptop",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "MagSafe • 100W GaN • Thunderbolt 4 • Milled Titanium",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setSelectedCategory("Phone Accessories")
                                    viewModel.navigateTo(ScreenDestination.Catalog)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Phones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = {
                                    viewModel.setSelectedCategory("Laptop Accessories")
                                    viewModel.navigateTo(ScreenDestination.Catalog)
                                },
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Laptops", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Live Inventory Alert Bar (if any low-stock items)
        if (lowStockProducts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF431407).copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFEA580C).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Flash Deal",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Real-Time Stock Warning",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFED7AA)
                            )
                            Text(
                                text = "${lowStockProducts.size} items nearing sell-out. Stock is reserved during checkout.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }

                        TextButton(
                            onClick = {
                                viewModel.navigateTo(ScreenDestination.Catalog)
                            }
                        ) {
                            Text("View", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Fast Category Switcher
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Explore Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryBannerCard(
                        title = "Phone Gear",
                        subtitle = "MagSafe, Cases, Fast GaN",
                        imageResName = "cat_phone_acc",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.setSelectedCategory("Phone Accessories")
                            viewModel.navigateTo(ScreenDestination.Catalog)
                        }
                    )

                    CategoryBannerCard(
                        title = "Laptop Gear",
                        subtitle = "Docks, Risers, Sleeves",
                        imageResName = "cat_laptop_acc",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.setSelectedCategory("Laptop Accessories")
                            viewModel.navigateTo(ScreenDestination.Catalog)
                        }
                    )
                }
            }
        }

        // Real-Time Inventory Flash Section
        if (lowStockProducts.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Trending",
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Low Stock Alerts (Live)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Live Inventory Sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSecurity,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lowStockProducts, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                isWishlisted = wishlistIds.contains(product.id),
                                onProductClick = {
                                    viewModel.navigateTo(ScreenDestination.ProductDetail(product.id))
                                },
                                onAddToCart = {
                                    viewModel.addToCart(product)
                                },
                                onToggleWishlist = {
                                    viewModel.toggleWishlist(product)
                                },
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }
            }
        }

        // Phone Accessories Showcase
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Phone Accessories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.setSelectedCategory("Phone Accessories")
                            viewModel.navigateTo(ScreenDestination.Catalog)
                        }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredPhoneProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onProductClick = {
                                viewModel.navigateTo(ScreenDestination.ProductDetail(product.id))
                            },
                            onAddToCart = {
                                viewModel.addToCart(product)
                            },
                            onToggleWishlist = {
                                viewModel.toggleWishlist(product)
                            },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
        }

        // Laptop Accessories Showcase
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laptop Accessories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium,
                        color = OrangePrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.setSelectedCategory("Laptop Accessories")
                            viewModel.navigateTo(ScreenDestination.Catalog)
                        }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredLaptopProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isWishlisted = wishlistIds.contains(product.id),
                            onProductClick = {
                                viewModel.navigateTo(ScreenDestination.ProductDetail(product.id))
                            },
                            onAddToCart = {
                                viewModel.addToCart(product)
                            },
                            onToggleWishlist = {
                                viewModel.toggleWishlist(product)
                            },
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
        }

        // Security & Trust Guarantees
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SecurityGuaranteesRow()
            }
        }
    }
}

@Composable
fun CategoryBannerCard(
    title: String,
    subtitle: String,
    imageResName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageRes = context.resources.getIdentifier(imageResName, "drawable", context.packageName).let {
        if (it != 0) it else com.example.R.drawable.ic_launcher_foreground
    }

    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF090D16).copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp
                )
            }
        }
    }
}
