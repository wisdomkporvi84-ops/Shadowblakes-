package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val subcategory: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val specificationsCsv: String,
    val compatibility: String,
    val stock: Int,
    val initialStock: Int,
    val isFeatured: Boolean,
    val isBestSeller: Boolean,
    val badge: String,
    val imageResName: String,
    val colorsCsv: String,
    val warrantyMonths: Int = 24
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val quantity: Int,
    val selectedColor: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val dateMillis: Long,
    val status: String,
    val totalAmount: Double,
    val itemsSummary: String,
    val itemsCount: Int,
    val paymentMethod: String,
    val paymentTxnId: String,
    val shippingAddress: String,
    val trackingNumber: String,
    val estimatedDelivery: String,
    val currentStepIndex: Int
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "user_101",
    val name: String,
    val email: String,
    val phone: String,
    val membershipTier: String,
    val rewardPoints: Int,
    val walletBalance: Double,
    val defaultAddress: String,
    val secondaryAddress: String
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_alerts")
data class InventoryAlertEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val productName: String,
    val message: String,
    val timestamp: Long,
    val isLowStock: Boolean
)
