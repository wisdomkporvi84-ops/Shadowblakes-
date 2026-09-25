package com.example

import com.example.data.model.OrderStatus
import com.example.data.model.Product
import org.junit.Assert.*
import org.junit.Test

class ShopLogicTest {

    @Test
    fun testProductDiscountCalculation() {
        val product = Product(
            id = "test_1",
            title = "TitanShield MagSafe Case",
            category = "Phone Accessories",
            subcategory = "Cases & Protection",
            price = 48.0,
            originalPrice = 60.0,
            rating = 4.9f,
            reviewCount = 100,
            description = "High grade protection",
            specifications = listOf("Titanium", "MagSafe"),
            compatibility = "iPhone 16 Pro",
            stock = 4,
            initialStock = 50,
            isFeatured = true,
            isBestSeller = true,
            badge = "Low Stock",
            imageResName = "cat_phone_acc",
            colors = listOf("Natural Titanium")
        )

        assertEquals(20, product.discountPercent)
        assertTrue(product.isLowStock)
        assertFalse(product.isOutOfStock)
    }

    @Test
    fun testOrderStatusSteps() {
        val confirmed = OrderStatus.CONFIRMED
        val delivered = OrderStatus.DELIVERED
        assertEquals("Order Confirmed", confirmed.label)
        assertEquals("Delivered", delivered.label)
    }
}
