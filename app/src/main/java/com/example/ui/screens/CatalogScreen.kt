package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.EmeraldSecurity
import com.example.ui.theme.OrangePrimary
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: ShopViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val wishlistIds by viewModel.wishlistIds.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
    val selectedSubcategory by viewModel.selectedSubcategoryFilter.collectAsState()

    var onlyLowStock by remember { mutableStateOf(false) }

    val categories = listOf("All", "Phone Accessories", "Laptop Accessories")

    val subcategories = when (selectedCategory) {
        "Phone Accessories" -> listOf("All", "MagSafe & Mounts", "Cases & Protection", "GaN Fast Chargers", "Cables & Adapters")
        "Laptop Accessories" -> listOf("All", "Hubs & Docks", "Stands & Ergonomics", "Laptop Sleeves & Bags", "Input Devices")
        else -> listOf("All", "MagSafe & Mounts", "Cases & Protection", "GaN Fast Chargers", "Hubs & Docks", "Stands & Ergonomics", "Input Devices")
    }

    val filteredProducts = products.filter { prod ->
        val matchesCategory = selectedCategory == "All" || prod.category.equals(selectedCategory, ignoreCase = true)
        val matchesSubcategory = selectedSubcategory == "All" || prod.subcategory.equals(selectedSubcategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                prod.title.contains(searchQuery, ignoreCase = true) ||
                prod.compatibility.contains(searchQuery, ignoreCase = true) ||
                prod.subcategory.contains(searchQuery, ignoreCase = true)
        val matchesStock = !onlyLowStock || prod.isLowStock

        matchesCategory && matchesSubcategory && matchesSearch && matchesStock
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_screen_grid"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar & Filter Header
        item(span = { GridItemSpan(2) }) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input"),
                    placeholder = { Text("Search chargers, docks, cases, models...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )
                )

                // Category Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedCategory(cat) },
                            label = {
                                Text(
                                    text = if (cat == "All") "All Catalog" else cat.replace(" Accessories", ""),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangePrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_chip_$cat")
                        )
                    }
                }

                // Subcategories
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subcategories) { sub ->
                        val isSelected = selectedSubcategory == sub
                        AssistChip(
                            onClick = { viewModel.setSelectedSubcategory(sub) },
                            label = { Text(text = sub, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                // Stock filter toggle & results count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredProducts.size} Accessories Found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onlyLowStock = !onlyLowStock }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Switch(
                            checked = onlyLowStock,
                            onCheckedChange = { onlyLowStock = it },
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            text = "Low Stock Deals Only",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (onlyLowStock) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Empty state
        if (filteredProducts.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "No items",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No accessories match your filter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Try clearing the search query or selecting 'All' categories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setSelectedCategory("All")
                            viewModel.setSelectedSubcategory("All")
                            onlyLowStock = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            items(filteredProducts, key = { it.id }) { product ->
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
                    }
                )
            }
        }
    }
}
