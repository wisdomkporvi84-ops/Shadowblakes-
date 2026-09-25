package com.example.data.model

data class Product(
    val id: String,
    val title: String,
    val category: String, // "Phone Accessories" or "Laptop Accessories"
    val subcategory: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val specifications: List<String>,
    val compatibility: String,
    val stock: Int,
    val initialStock: Int,
    val isFeatured: Boolean,
    val isBestSeller: Boolean,
    val badge: String,
    val imageResName: String,
    val colors: List<String>,
    val warrantyMonths: Int = 24
) {
    val discountPercent: Int
        get() = if (originalPrice > price) (((originalPrice - price) / originalPrice) * 100).toInt() else 0

    val isLowStock: Boolean
        get() = stock in 1..5

    val isOutOfStock: Boolean
        get() = stock <= 0
}

data class CartItem(
    val id: Long = 0,
    val product: Product,
    val quantity: Int,
    val selectedColor: String
)

data class Order(
    val orderId: String,
    val dateMillis: Long,
    val status: OrderStatus,
    val totalAmount: Double,
    val itemsSummary: String,
    val itemsCount: Int,
    val paymentMethod: String,
    val paymentTxnId: String,
    val shippingAddress: String,
    val trackingNumber: String,
    val estimatedDelivery: String,
    val currentStepIndex: Int // 0: Confirmed, 1: Packed, 2: Dispatched, 3: Out for Delivery, 4: Delivered
)

enum class OrderStatus(val label: String) {
    CONFIRMED("Order Confirmed"),
    PROCESSING("Security Packed"),
    DISPATCHED("Dispatched (Tokyo Hub)"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered")
}

data class UserProfile(
    val id: String = "user_101",
    val name: String = "Kenji Takahashi",
    val email: String = "kenji.tech@asiahouse.shop",
    val phone: String = "+1 (555) 234-8900",
    val membershipTier: String = "Black VIP Member",
    val rewardPoints: Int = 3450,
    val walletBalance: Double = 420.00,
    val defaultAddress: String = "84 Orchard Boulevard, Unit #14-02, Orchard Central, Singapore 238826",
    val secondaryAddress: String = "7-1 Roppongi Hills Mori Tower, Minato City, Tokyo 106-6108, Japan"
)

enum class PaymentType(val title: String, val subtitle: String, val badge: String) {
    ASIA_HOUSE_PAY("Asia House Pay", "Fast 1-tap checkout • 5% Cashback", "Instant"),
    CREDIT_CARD("Credit / Debit Card", "256-Bit SSL • 3D Secure 2.0 Auth", "Encrypted"),
    GOOGLE_PAY("Google Pay", "Fast biometric authentication", "Zero-Fraud"),
    BNPL("Pay in 4 Installments", "0% interest with Asia House Flex", "Flexible"),
    CASH_ON_DELIVERY("Cash on Delivery", "Pay upon physical inspection", "Verified")
}

data class InventoryAlert(
    val id: String,
    val productId: String,
    val productName: String,
    val message: String,
    val timestamp: Long,
    val isLowStock: Boolean
)
